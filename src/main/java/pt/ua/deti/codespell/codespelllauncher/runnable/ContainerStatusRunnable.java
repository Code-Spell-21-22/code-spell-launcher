package pt.ua.deti.codespell.codespelllauncher.runnable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pt.ua.deti.codespell.codespelllauncher.code.report.CodeExecReport;
import pt.ua.deti.codespell.codespelllauncher.code.results.output.*;
import pt.ua.deti.codespell.codespelllauncher.code.results.output.implementation.*;
import pt.ua.deti.codespell.codespelllauncher.code.results.status.CodeExecutionStatus;
import pt.ua.deti.codespell.codespelllauncher.containers.ContainerLauncherManager;
import pt.ua.deti.codespell.codespelllauncher.containers.ContainerRegistry;
import pt.ua.deti.codespell.codespelllauncher.model.CodeExecutionInstance;
import pt.ua.deti.codespell.codespelllauncher.rabbitmq.RabbitMQSender;
import pt.ua.deti.codespell.codespelllauncher.redis.service.CodeExecReportService;
import pt.ua.deti.codespell.codespelllauncher.response.CodeLauncherResponse;

import java.util.UUID;

@Component
@Log4j2
public class ContainerStatusRunnable implements Runnable {

    private final ContainerLauncherManager containerLauncherManager;
    private final ContainerRegistry containerRegistry;
    private final CodeExecReportService codeExecReportService;
    private final RabbitMQSender rabbitMQSender;

    @Autowired
    public ContainerStatusRunnable(ContainerLauncherManager containerLauncherManager, ContainerRegistry containerRegistry, CodeExecReportService codeExecReportService, RabbitMQSender rabbitMQSender) {
        this.containerLauncherManager = containerLauncherManager;
        this.containerRegistry = containerRegistry;
        this.codeExecReportService = codeExecReportService;
        this.rabbitMQSender = rabbitMQSender;
    }

    @Override
    public void run() {

        for (CodeExecutionInstance currentCodeExecutionInstance : containerRegistry.getAllRegisters()) {

            boolean containerExists = containerLauncherManager.containerExists(currentCodeExecutionInstance);

            if (!containerExists) {
                containerLauncherManager.discardLaunchedContainer(currentCodeExecutionInstance);
                return;
            }

            boolean isExecutorUp = containerLauncherManager.checkExecutorStatus(currentCodeExecutionInstance);

            // If docker container is still running ignore this tick
            if (isExecutorUp) return;

            OutputRegisterHandler outputRegisterHandler = new OutputRegisterHandler(containerLauncherManager);

            outputRegisterHandler.registerOutput(new CodeAnalysisOutput(currentCodeExecutionInstance));
            outputRegisterHandler.registerOutput(new CodeExecutionResultOutput(currentCodeExecutionInstance));
            outputRegisterHandler.registerOutput(new ErrorsOutput(currentCodeExecutionInstance));
            outputRegisterHandler.registerOutput(new ExecutionOutput(currentCodeExecutionInstance));
            outputRegisterHandler.registerOutput(new RuntimeLogsOutput(currentCodeExecutionInstance));
            outputRegisterHandler.registerOutput(new StepsOutput(currentCodeExecutionInstance));

            outputRegisterHandler.pullOutputFiles();

            CodeExecReport codeExecReport = outputRegisterHandler.createExecutionReport();
            log.info(String.format("Code Exec Report Generated (%s)", codeExecReport));

            codeExecReportService.save(codeExecReport);
            log.info("Code Exec Report saved to Redis.");

            containerLauncherManager.discardLaunchedContainer(currentCodeExecutionInstance);

            CodeLauncherResponse codeLauncherResponse = new CodeLauncherResponse(UUID.fromString(codeExecReport.getId()), CodeExecutionStatus.RAN);
            String codeLauncherResponseJSON;

            try {
                codeLauncherResponseJSON = codeLauncherResponse.toJson();
            } catch (JsonProcessingException e) {
                log.warn("Error converting CodeLauncherResponse to JSON string.");
                return;
            }

            rabbitMQSender.sendMessage("reports", codeLauncherResponseJSON);

        }

    }

}
