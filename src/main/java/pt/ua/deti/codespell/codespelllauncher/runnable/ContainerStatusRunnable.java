package pt.ua.deti.codespell.codespelllauncher.runnable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pt.ua.deti.codespell.codespelllauncher.containers.ContainerLauncherManager;
import pt.ua.deti.codespell.codespelllauncher.containers.ContainerRegistry;

import java.util.UUID;

@Component
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

        for (UUID codeUniqueId : containerRegistry.getAllRegisters()) {

            boolean isExecutorUp = containerLauncherManager.checkExecutorStatus(codeUniqueId);

            if (!isExecutorUp) {

                containerLauncherManager.pullAnalysisData(codeUniqueId);



            }

        }

    }

}
