package pt.ua.deti.codespell.codespelllauncher.runnable;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pt.ua.deti.codespell.codespelllauncher.code.results.entities.CodeAnalysisOutputEntity;
import pt.ua.deti.codespell.codespelllauncher.code.results.entities.CodeExecutionResultEntity;
import pt.ua.deti.codespell.codespelllauncher.code.results.output.CodeAnalysisOutput;
import pt.ua.deti.codespell.codespelllauncher.code.results.output.CodeExecutionResultOutput;
import pt.ua.deti.codespell.codespelllauncher.code.results.status.CodeExecutionStatus;
import pt.ua.deti.codespell.codespelllauncher.containers.ContainerLauncherManager;
import pt.ua.deti.codespell.codespelllauncher.containers.ContainerRegistry;
import pt.ua.deti.codespell.codespelllauncher.model.CodeExecutionInstance;
import pt.ua.deti.codespell.codespelllauncher.rabbitmq.RabbitMQSender;

@Component
@Log4j2
public class ContainerStatusRunnable implements Runnable {

    private final ContainerLauncherManager containerLauncherManager;
    private final ContainerRegistry containerRegistry;
    private final RabbitMQSender rabbitMQSender;

    @Autowired
    public ContainerStatusRunnable(ContainerLauncherManager containerLauncherManager, ContainerRegistry containerRegistry, RabbitMQSender rabbitMQSender) {
        this.containerLauncherManager = containerLauncherManager;
        this.containerRegistry = containerRegistry;
        this.rabbitMQSender = rabbitMQSender;
    }

    @Override
    public void run() {

        for (CodeExecutionInstance currentCodeExecutionInstance : containerRegistry.getAllRegisters()) {

            CodeAnalysisOutput analysisOutput = new CodeAnalysisOutput(currentCodeExecutionInstance);
            CodeExecutionResultOutput executionResultOutput = new CodeExecutionResultOutput(currentCodeExecutionInstance);

            boolean containerExists = containerLauncherManager.containerExists(currentCodeExecutionInstance);

            if (!containerExists) {
                containerLauncherManager.discardLaunchedContainer(currentCodeExecutionInstance);
                return;
            }

            boolean isExecutorUp = containerLauncherManager.checkExecutorStatus(currentCodeExecutionInstance);

            // If the executor has finished its activity
            if (!isExecutorUp) {

                containerLauncherManager.pullData(currentCodeExecutionInstance, analysisOutput);

                CodeAnalysisOutputEntity codeAnalysisResult = analysisOutput.toEntity();

                if (codeAnalysisResult == null) {
                    log.warn(String.format("Analysis Result entity for code %s is null.", currentCodeExecutionInstance));
                    return;
                }

                String codeAnalysisResultJson = codeAnalysisResult.toJsonString();

                if (codeAnalysisResultJson != null) {
                    rabbitMQSender.sendMessage("analysis.results", codeAnalysisResultJson);
                    log.info(String.format("Sent analysis result for code %s.", currentCodeExecutionInstance));
                }

                if (codeAnalysisResult.getAnalysisStatus() == CodeExecutionStatus.SUCCESS) {

                    containerLauncherManager.pullData(currentCodeExecutionInstance, executionResultOutput);

                    CodeExecutionResultEntity codeExecutionResultEntity = executionResultOutput.toEntity();

                    if (codeExecutionResultEntity == null) {
                        log.warn(String.format("Code Execution Result entity for code %s is null.", currentCodeExecutionInstance));
                        return;
                    }

                    String codeExecutionResultJson = codeExecutionResultEntity.toJsonString();

                    if (codeExecutionResultJson != null) {
                        rabbitMQSender.sendMessage("execution.results", codeExecutionResultJson);
                        log.info(String.format("Sent execution result for code %s.", currentCodeExecutionInstance));
                    }

                }

                containerLauncherManager.discardLaunchedContainer(currentCodeExecutionInstance);

            }

        }

    }

}
