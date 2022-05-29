package pt.ua.deti.codespell.codespelllauncher.runnable;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pt.ua.deti.codespell.codespelllauncher.code.report.CodeExecReport;
import pt.ua.deti.codespell.codespelllauncher.code.results.output.*;
import pt.ua.deti.codespell.codespelllauncher.code.results.output.implementation.*;
import pt.ua.deti.codespell.codespelllauncher.containers.ContainerLauncherManager;
import pt.ua.deti.codespell.codespelllauncher.containers.ContainerRegistry;
import pt.ua.deti.codespell.codespelllauncher.model.CodeExecutionInstance;

@Component
@Log4j2
public class ContainerStatusRunnable implements Runnable {

    private final ContainerLauncherManager containerLauncherManager;
    private final ContainerRegistry containerRegistry;

    @Autowired
    public ContainerStatusRunnable(ContainerLauncherManager containerLauncherManager, ContainerRegistry containerRegistry) {
        this.containerLauncherManager = containerLauncherManager;
        this.containerRegistry = containerRegistry;
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
            log.info("Code Exec Report Generated: " + codeExecReport);

            containerLauncherManager.discardLaunchedContainer(currentCodeExecutionInstance);

        }

    }

}
