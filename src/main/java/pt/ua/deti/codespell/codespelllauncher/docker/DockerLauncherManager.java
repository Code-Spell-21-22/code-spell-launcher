package pt.ua.deti.codespell.codespelllauncher.docker;

import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Image;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pt.ua.deti.codespell.codespelllauncher.model.CodeExecutionInstance;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
public class DockerLauncherManager {

    private final DockerAPIHandler dockerAPIHandler;

    @Autowired
    public DockerLauncherManager(DockerAPIHandler dockerAPIHandler) {
        this.dockerAPIHandler = dockerAPIHandler;
    }

    public void launchNewProcessor(CodeExecutionInstance codeExecutionInstance) {

        Set<String> tags = Set.of("code_spell_launcher_executor:latest");
        File processorDockerFile = new File(FileUtils.getUserDirectoryPath() + File.separator + "Code_Spell" + File.separator + "Launcher" + File.separator + "Dockerfile");
        String containerName = String.format("Code_Spell_Executor_%s", codeExecutionInstance.getCodeUniqueId());

        try {
            System.out.println(FileUtils.readFileToString(processorDockerFile, StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<Container> containerList = dockerAPIHandler.getListOfContainersByName(List.of(containerName));
        String containerId = null;

        if (containerList.size() == 0) {

            Optional<Image> processorImage;

            System.out.println("Images list by name: " + dockerAPIHandler.getListOfImagesByName("code"));

            if (dockerAPIHandler.getListOfImagesByName("code").size() == 0) {
                System.out.println("Start build...");
                String imageId = dockerAPIHandler.buildImage(processorDockerFile, tags, new HashMap<>()).awaitImageId();
                System.out.println("Finished build...");
                processorImage = dockerAPIHandler.getImageById(imageId);
            } else {
                processorImage = dockerAPIHandler.getListOfImagesByName("code").stream().findFirst();
            }

            if (processorImage.isPresent())
                containerId = dockerAPIHandler.createDockerContainer(containerName, processorImage.get());

        } else {
            containerId = containerList.get(0).getId();
        }

        System.out.println("COntainer ID: " + containerId);

        dockerAPIHandler.startContainer(containerId);

    }


}
