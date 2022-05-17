package pt.ua.deti.codespell.codespelllauncher.rabbitmq;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pt.ua.deti.codespell.codespelllauncher.code.CodeExecutorHandler;
import pt.ua.deti.codespell.codespelllauncher.containers.ContainerLauncherManager;
import pt.ua.deti.codespell.codespelllauncher.model.CodeExecutionInstance;

import java.nio.charset.StandardCharsets;

@Component
@Log4j2
public class RabbitMQReceiver {

    private final ContainerLauncherManager containerLauncherManager;

    @Autowired
    public RabbitMQReceiver(ContainerLauncherManager containerLauncherManager) {
        this.containerLauncherManager = containerLauncherManager;
    }

    public void receiveMessage(byte[] messageBytes) {
        String message = new String(messageBytes, StandardCharsets.UTF_8);
        processMessage(message);
        log.info("Received message: " + message);
    }

    public void receiveMessage(String message) {
        processMessage(message);
        log.info("Received message: " + message);
    }

    private void processMessage(String message) {

        CodeExecutionInstance codeExecutionInstance;

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
            codeExecutionInstance = objectMapper.readValue(message, CodeExecutionInstance.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return;
        }

        CodeExecutorHandler codeExecutorHandler = new CodeExecutorHandler(codeExecutionInstance);

        codeExecutorHandler.initDirectory();

        if (codeExecutorHandler.injectCode(codeExecutionInstance.getChapter(), codeExecutionInstance.getLevel())) {
            log.info(String.format("Code successfully injected for code execution request (%s).", codeExecutionInstance.getCodeUniqueId()));
        } else {
            log.warn(String.format("Error occurred while injecting code for code execution request (%s).", codeExecutionInstance.getCodeUniqueId()));
            return;
        }

        containerLauncherManager.launchNewProcessor(codeExecutionInstance);

    }

}
