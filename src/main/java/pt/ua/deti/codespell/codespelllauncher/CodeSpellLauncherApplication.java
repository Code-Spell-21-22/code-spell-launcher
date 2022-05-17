package pt.ua.deti.codespell.codespelllauncher;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import pt.ua.deti.codespell.codespelllauncher.code.CodeExecutorHandler;
import pt.ua.deti.codespell.codespelllauncher.rabbitmq.RabbitMQReceiver;
import pt.ua.deti.codespell.codespelllauncher.rabbitmq.RabbitMQSender;

@SpringBootApplication
@Log4j2
public class CodeSpellLauncherApplication {

    private static RabbitMQSender rabbitMQSender;
    private static RabbitMQReceiver rabbitMQReceiver;

    @Autowired
    public CodeSpellLauncherApplication(RabbitMQSender rabbitMQSender, RabbitMQReceiver rabbitMQReceiver) {
        CodeSpellLauncherApplication.rabbitMQSender = rabbitMQSender;
        CodeSpellLauncherApplication.rabbitMQReceiver = rabbitMQReceiver;
    }

    public static void main(String[] args) {

        SpringApplication.run(CodeSpellLauncherApplication.class, args);

        if (checkCentralRepository())
            log.info("Central Repository Ready!");
        else
            log.warn("Central Repository not found.");

    }

    private static boolean checkCentralRepository() {
        return CodeExecutorHandler.initialCheck();
    }

}
