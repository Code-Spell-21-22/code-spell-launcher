package pt.ua.deti.codespell.codespelllauncher.runnable;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pt.ua.deti.codespell.codespelllauncher.code.results.pojo.CodeAnalysisResult;
import pt.ua.deti.codespell.codespelllauncher.code.results.AnalysisCodeResult;
import pt.ua.deti.codespell.codespelllauncher.containers.ContainerLauncherManager;
import pt.ua.deti.codespell.codespelllauncher.containers.ContainerRegistry;
import pt.ua.deti.codespell.codespelllauncher.model.CodeExecutionInstance;
import pt.ua.deti.codespell.codespelllauncher.rabbitmq.RabbitMQSender;
import pt.ua.deti.codespell.codespelllauncher.response.CodeLauncherResponse;

import java.io.IOException;

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

        for (CodeExecutionInstance codeExecutionInstance : containerRegistry.getAllRegisters()) {

            boolean containerExists = containerLauncherManager.containerExists(codeExecutionInstance);

            if (!containerExists) {
                containerLauncherManager.removeContainerFromRegistry(codeExecutionInstance);
                return;
            }

            boolean isExecutorUp = containerLauncherManager.checkExecutorStatus(codeExecutionInstance);

            if (!isExecutorUp) {

                AnalysisCodeResult analysisCodeResult = new AnalysisCodeResult(codeExecutionInstance);

                containerLauncherManager.pullData(codeExecutionInstance, analysisCodeResult);

                CodeAnalysisResult codeAnalysisResult;

                try {
                    codeAnalysisResult = analysisCodeResult.toEntity();
                } catch (IOException ioException) {
                    log.warn(String.format("Unable to transform Analysis Result to entity for code %s.", codeExecutionInstance));
                    ioException.printStackTrace();
                    return;
                }

                if (codeAnalysisResult == null) {
                    log.warn(String.format("Analysis Result entity for code %s is null.", codeExecutionInstance));
                    return;
                }

                CodeLauncherResponse codeLauncherResponse = codeAnalysisResult.toCodeLauncherResponse();
                String codeLauncherResponseJson;

                try {
                    codeLauncherResponseJson = codeLauncherResponse.toJson();
                } catch (JsonProcessingException jsonProcessingException) {
                    log.warn(String.format("Unable to convert Code Launcher Response entity to JSON for code %s.", codeExecutionInstance));
                    return;
                }

                rabbitMQSender.sendMessage("results", codeLauncherResponseJson);
                log.info(String.format("Sent code result for code %s.", codeExecutionInstance));

                try {
                    containerLauncherManager.cleanContainerTempData(codeExecutionInstance);
                } catch (IOException ioException) {
                    log.warn(String.format("Unable to clean temp data for code instance %s.", codeExecutionInstance));
                }

                containerLauncherManager.removeContainer(codeExecutionInstance);
                containerLauncherManager.removeContainerFromRegistry(codeExecutionInstance);

            }

        }

    }

}
