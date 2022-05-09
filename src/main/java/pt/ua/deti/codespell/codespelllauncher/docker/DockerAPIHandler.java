package pt.ua.deti.codespell.codespelllauncher.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.BuildImageResultCallback;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.DockerClientBuilder;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Component
public class DockerAPIHandler {

    DockerClient dockerClient = DockerClientBuilder.getInstance().build();

    public List<Container> getListOfContainers() {
        return dockerClient.listContainersCmd().withShowAll(true).exec();
    }

    public List<Container> getListOfContainersByName(List<String> containersNames) {
        return dockerClient.listContainersCmd().withShowAll(true).withNameFilter(containersNames).exec();
    }

    public List<Image> getListOfImages() {
        return dockerClient.listImagesCmd().exec();
    }

    public List<Image> getListOfImagesByName(String imageName) {
        return dockerClient.listImagesCmd().withImageNameFilter(imageName).exec();
    }

    public Optional<Image> getImageById(String imageId) {
        return getListOfImages().stream().filter(image -> image.getId().equals(imageId)).findFirst();
    }

    public BuildImageResultCallback buildImage(File imageFile, Set<String> tags, Map<String, String> args)  {

        System.out.println("Building image");

        try {
            return dockerClient.buildImageCmd()
                    .withDockerfile(imageFile)
                    .withBuildArg("codeUniqueId", "24b5c104-5530-4125-9b3a-093fd7d88e8d")
                    .withTags(Set.of("code"))
                    .exec(new BuildImageResultCallback())
                    .awaitCompletion();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String createDockerContainer(String containerName, Image image) {
        return dockerClient.createContainerCmd(image.getId()).withName(containerName).exec().getId();
    }

    public void startContainer(String containerId) {
        dockerClient.startContainerCmd(containerId).exec();
    }

}
