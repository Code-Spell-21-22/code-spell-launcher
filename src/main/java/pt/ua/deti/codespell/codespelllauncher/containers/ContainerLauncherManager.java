package pt.ua.deti.codespell.codespelllauncher.containers;

import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Image;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pt.ua.deti.codespell.codespelllauncher.containers.ContainerRegistry;
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

        if (containerList.isEmpty()) {
            containerId = dockerAPIHandler.createDockerContainer(containerName, processorImage, containerEnvVars);
        } else {
            containerId = containerList.get(0).getId();
        }

        dockerAPIHandler.copyToContainer(containerId, codeExecutorDirectory);
        dockerAPIHandler.startContainer(containerId);

        containerRegistry.register(codeExecutionInstance.getCodeUniqueId(), containerId);

    }

    public boolean checkExecutorStatus(UUID codeUniqueId) {

        if (!containerRegistry.isRegistered(codeUniqueId))
            return false;

        String containerId = containerRegistry.getRegistry(codeUniqueId);
        return dockerAPIHandler.isContainerRunning(containerId);

    }

    public void pullAnalysisData(UUID codeUniqueId) {

        if (!containerRegistry.isRegistered(codeUniqueId))
            return;

        String containerId = containerRegistry.getRegistry(codeUniqueId);
        String sourceFilePath = File.separator + "analysis.txt";
        String destinationFilePath = FileUtils.getUserDirectoryPath() + File.separator + "Code_Spell" + File.separator + "Launcher" + File.separator + "Output" + File.separator + codeUniqueId.toString() + File.separator + "analysis.txt";

        File destinationFile = new File(destinationFilePath);

        try {
            if (!destinationFile.exists()) {
                if (!destinationFile.getParentFile().mkdirs() || !destinationFile.createNewFile()) {
                    log.warn("Unable to create destination file to receive Code Analysis Data.");
                }
            }
        } catch (Exception e) {
            log.warn("Unable to create destination file to receive Code Analysis Data.");
            e.printStackTrace();
        }

        dockerAPIHandler.pullFromContainer(containerId, sourceFilePath, destinationFilePath);
        destinationFile.deleteOnExit();

    }

}
