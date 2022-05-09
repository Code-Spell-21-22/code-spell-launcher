package pt.ua.deti.codespell.codespelllauncher;

import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.Container;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import pt.ua.deti.codespell.codespelllauncher.code.CodeExecutorHandler;
import pt.ua.deti.codespell.codespelllauncher.docker.DockerAPIHandler;
import pt.ua.deti.codespell.codespelllauncher.rabbitmq.RabbitMQHandler;
import pt.ua.deti.codespell.codespelllauncher.rabbitmq.RabbitMQReceiver;
import pt.ua.deti.codespell.codespelllauncher.rabbitmq.RabbitMQSender;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class CodeSpellLauncherApplication {

    private static RabbitMQSender rabbitMQSender;
    private static RabbitMQReceiver rabbitMQReceiver;

    @Autowired
    public CodeSpellLauncherApplication(RabbitMQSender rabbitMQSender, RabbitMQReceiver rabbitMQReceiver) {
        CodeSpellLauncherApplication.rabbitMQSender = rabbitMQSender;
        CodeSpellLauncherApplication.rabbitMQReceiver = rabbitMQReceiver;
    }

    public static void main(String[] args) throws InterruptedException {

        SpringApplication.run(CodeSpellLauncherApplication.class, args);

        System.out.println("Central Repository Status: " + CodeExecutorHandler.initialCheck());

    }

}
