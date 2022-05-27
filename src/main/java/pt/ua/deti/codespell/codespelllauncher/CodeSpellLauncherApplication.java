package pt.ua.deti.codespell.codespelllauncher;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import pt.ua.deti.codespell.codespelllauncher.code.CodeExecutorHandler;
import pt.ua.deti.codespell.codespelllauncher.code.results.output.OutputRegisterHandler;

@SpringBootApplication
@Log4j2
public class CodeSpellLauncherApplication {

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
