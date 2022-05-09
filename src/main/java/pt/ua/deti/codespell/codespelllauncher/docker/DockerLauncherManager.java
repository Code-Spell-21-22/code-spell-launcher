package pt.ua.deti.codespell.codespelllauncher.docker;

import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Image;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pt.ua.deti.codespell.codespelllauncher.model.CodeExecutionInstance;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
@Log4j2
public class DockerLauncherManager {

    private final DockerAPIHandler dockerAPIHandler;

    @Autowired
    public DockerLauncherManager(DockerAPIHandler dockerAPIHandler) {
        this.dockerAPIHandler = dockerAPIHandler;
    }

    public void launchNewProcessor(CodeExecutionInstance codeExecutionInstance) {

        String tag = "code_spell_code_executor";
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

        if (containerList.isEmpty()) {
            containerId = dockerAPIHandler.createDockerContainer(containerName, processorImage, "CODE_ID=" + codeExecutionInstance.getCodeUniqueId().toString());
        } else {
            containerId = containerList.get(0).getId();
        }

        dockerAPIHandler.copyToContainer(containerId, codeExecutorDirectory);
        dockerAPIHandler.startContainer(containerId);

    }

}
