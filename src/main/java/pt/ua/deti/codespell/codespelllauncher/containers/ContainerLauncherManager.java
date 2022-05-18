package pt.ua.deti.codespell.codespelllauncher.containers;

import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Image;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pt.ua.deti.codespell.codespelllauncher.code.results.CodeExecutionResult;
import pt.ua.deti.codespell.codespelllauncher.docker.DockerAPIHandler;
import pt.ua.deti.codespell.codespelllauncher.model.CodeExecutionInstance;

import java.io.*;
import java.util.*;

@Component
@Log4j2
public class ContainerLauncherManager {

    private final DockerAPIHandler dockerAPIHandler;
    private final ContainerRegistry containerRegistry;

    @Autowired
    public ContainerLauncherManager(DockerAPIHandler dockerAPIHandler, ContainerRegistry containerRegistry) {
        this.dockerAPIHandler = dockerAPIHandler;
        this.containerRegistry = containerRegistry;
    }

    public void launchNewProcessor(CodeExecutionInstance codeExecutionInstance) {

        String tag = "code_spell_code_executor:dev";
        String containerName = String.format("Code_Spell_Executor_%s", codeExecutionInstance.getCodeUniqueId());
        File codeExecutorDirectory = new File(FileUtils.getUserDirectoryPath() + File.separator + "Code_Spell" + File.separator + "Launcher" + File.separator + "Temp" + File.separator + codeExecutionInstance.getCodeUniqueId().toString() + File.separator + "code-spell-code-executor");

        List<Image> images = dockerAPIHandler.getListOfImagesByName(tag);
        List<Container> containerList = dockerAPIHandler.getListOfContainersByName(List.of(containerName));

        if (images.isEmpty()) {
           log.warn(String.format("Unable to find %s image. The container could not be launched...", tag));
            return;
        }

        Image processorImage = images.stream().findFirst().get();
        String containerId;
        String[] containerEnvVars = {
                "CODE_ID=" + codeExecutionInstance.getCodeUniqueId().toString(),
                "CHAPTER_NUMBER=" + codeExecutionInstance.getChapter(),
                "LEVEL_NUMBER=" + codeExecutionInstance.getLevel()
        };

        System.out.println(Arrays.toString(containerEnvVars));

        if (containerList.isEmpty()) {
            containerId = dockerAPIHandler.createDockerContainer(containerName, processorImage, containerEnvVars);
        } else {
            containerId = containerList.get(0).getId();
        }

        dockerAPIHandler.copyToContainer(containerId, codeExecutorDirectory);
        dockerAPIHandler.startContainer(containerId);

        containerRegistry.register(codeExecutionInstance, containerId);

    }

    public boolean checkExecutorStatus(CodeExecutionInstance codeExecutionInstance) {

        if (!containerRegistry.isRegistered(codeExecutionInstance))
            return false;

        String containerId = containerRegistry.getRegistry(codeExecutionInstance);
        return dockerAPIHandler.isContainerRunning(containerId);

    }

    public void pullData(CodeExecutionInstance codeExecutionInstance, CodeExecutionResult<?> codeExecutionResult) {

        if (!containerRegistry.isRegistered(codeExecutionInstance))
            return;

        File destinationFile = codeExecutionResult.getDestinationFile();

        String containerId = containerRegistry.getRegistry(codeExecutionInstance);
        String sourceFilePath = codeExecutionResult.getSourceFile().getAbsolutePath();
        String destinationFilePath = destinationFile.getAbsolutePath();

        try {
            if (!destinationFile.exists()) {
                if (!destinationFile.getParentFile().mkdirs() || !destinationFile.createNewFile()) {
                    log.warn("Unable to create destination file to receive container pulled data.");
                }
            }
        } catch (Exception e) {
            log.warn("Unable to create destination file to receive container pulled data.");
            e.printStackTrace();
        }

        dockerAPIHandler.pullFromContainer(containerId, sourceFilePath, destinationFilePath);
        destinationFile.deleteOnExit();

    }

    public boolean containerExists(CodeExecutionInstance codeExecutionInstance) {

        if (!containerRegistry.isRegistered(codeExecutionInstance))
            return false;

        String containerId = containerRegistry.getRegistry(codeExecutionInstance);
        return dockerAPIHandler.getListOfContainers().stream().anyMatch(container -> container.getId().equals(containerId));

    }

    public void removeContainerFromRegistry(CodeExecutionInstance codeExecutionInstance) {

        if (!containerRegistry.isRegistered(codeExecutionInstance))
            return;

        String containerId = containerRegistry.getRegistry(codeExecutionInstance);
        containerRegistry.unregister(codeExecutionInstance);

        log.info(String.format("Removed container from registry (%s).", containerId));

    }

    public void removeContainer(CodeExecutionInstance codeExecutionInstance) {

        if (!containerRegistry.isRegistered(codeExecutionInstance))
            return;

        String containerId = containerRegistry.getRegistry(codeExecutionInstance);

        dockerAPIHandler.removeContainer(containerId);

        log.info(String.format("Removed container from docker engine (%s).", containerId));

    }

    public void cleanContainerTempData(CodeExecutionInstance codeExecutionInstance) throws IOException {

        File tempContainerDirectory = new File(FileUtils.getUserDirectoryPath() + File.separator + "Code_Spell" + File.separator + "Launcher" + File.separator + "Temp" + File.separator + codeExecutionInstance.getCodeUniqueId());
        File outputContainerDirectory = new File(FileUtils.getUserDirectoryPath() + File.separator + "Code_Spell" + File.separator + "Launcher" + File.separator + "Output" + File.separator + codeExecutionInstance.getCodeUniqueId());

        FileUtils.deleteDirectory(tempContainerDirectory);
        FileUtils.deleteDirectory(outputContainerDirectory);

    }

}
