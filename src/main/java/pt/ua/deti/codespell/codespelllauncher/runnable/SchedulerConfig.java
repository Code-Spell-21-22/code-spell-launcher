package pt.ua.deti.codespell.codespelllauncher.runnable;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
@Log4j2
public class SchedulerConfig {

    private final ContainerStatusRunnable containerStatusRunnable;

    @Autowired
    public SchedulerConfig(ContainerStatusRunnable containerStatusRunnable) {
        this.containerStatusRunnable = containerStatusRunnable;
    }

    @Scheduled(initialDelay = 1000, fixedDelay = 5000)
    public void checkContainerStatus() {
        containerStatusRunnable.run();
    }

}
