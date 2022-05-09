package pt.ua.deti.codespell.codespelllauncher.rabbitmq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pt.ua.deti.codespell.codespelllauncher.code.CodeExecutorHandler;
import pt.ua.deti.codespell.codespelllauncher.docker.DockerLauncherManager;
import pt.ua.deti.codespell.codespelllauncher.model.CodeExecutionInstance;

import java.nio.charset.StandardCharsets;

@Component
public class RabbitMQReceiver {

    private final DockerLauncherManager dockerLauncherManager;

    @Autowired
    public RabbitMQReceiver(DockerLauncherManager dockerLauncherManager) {
        this.dockerLauncherManager = dockerLauncherManager;
    }

    public void receiveMessage(byte[] messageBytes) {

        String message = new String(messageBytes, StandardCharsets.UTF_8);

        processMessage(message);
        System.out.println("Received < " + message);

    }

    public void receiveMessage(String message) {
        processMessage(message);
        System.out.println("Received < " + message);
    }

    private void processMessage(String message) {

        CodeExecutionInstance codeExecutionInstance;

        try {
            codeExecutionInstance = new ObjectMapper().readValue(message, CodeExecutionInstance.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return;
        }

        CodeExecutorHandler codeExecutorHandler = new CodeExecutorHandler(codeExecutionInstance);

        codeExecutorHandler.initDirectory();

        System.out.println("Code Injection Result: " + codeExecutorHandler.injectCode());
        System.out.println(codeExecutionInstance.getCodeUniqueId());

        dockerLauncherManager.launchNewProcessor(codeExecutionInstance);

    }

}
