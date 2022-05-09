package pt.ua.deti.codespell.codespelllauncher.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.BuildImageResultCallback;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.DockerClientBuilder;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

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

    public List<Image> getListOfImagesByName(String imageTag) {
        List<Image> images = dockerClient.listImagesCmd().exec();
        return images.stream()
                .filter(image -> Arrays.asList(image.getRepoTags()).contains(imageTag))
                .collect(Collectors.toList());
    }

    public Optional<Image> getImageById(String imageId) {
        return getListOfImages().stream().filter(image -> image.getId().equals(imageId)).findFirst();
    }

    public BuildImageResultCallback buildImage(File imageFile, String tag, String argKey, String argValue)  {

        try {
            return dockerClient.buildImageCmd()
                    .withDockerfile(imageFile)
                    .withBuildArg(argKey, argValue)
                    .withTag(tag)
                    .exec(new BuildImageResultCallback())
                    .awaitCompletion();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return null;

    }

    public String createDockerContainer(String containerName, Image image, String... envVars) {
        return dockerClient.createContainerCmd(image.getId()).withName(containerName).withEnv(envVars).exec().getId();
    }

    public void startContainer(String containerId) {
        dockerClient.startContainerCmd(containerId).exec();
    }

    public void copyToContainer(String containerId, File file) {
        dockerClient.copyArchiveToContainerCmd(containerId).withHostResource(file.getAbsolutePath()).exec();
    }

}
