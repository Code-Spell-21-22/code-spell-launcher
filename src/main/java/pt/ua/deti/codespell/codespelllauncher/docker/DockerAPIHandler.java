package pt.ua.deti.codespell.codespelllauncher.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.BuildImageResultCallback;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.DockerClientBuilder;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class DockerAPIHandler {

    private final DockerClient dockerClient = DockerClientBuilder.getInstance().build();

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

    public Boolean isContainerRunning(String containerId) {
        return dockerClient.inspectContainerCmd(containerId).exec().getState().getRunning();
    }

    public Boolean pullFromContainer(String containerId, String sourceFilePath, String destinationFilePath) {

        TarArchiveInputStream archiveInputStream = new TarArchiveInputStream(dockerClient.copyArchiveFromContainerCmd(containerId, sourceFilePath).exec());

        try {

            TarArchiveEntry entry;
            int bufferSize = 8192;

            while ((entry = (TarArchiveEntry) archiveInputStream.getNextEntry()) != null) {

                if (entry.isDirectory()) {
                    new File(destinationFilePath).mkdirs();
                } else {

                    int count;
                    byte[] data = new byte[bufferSize];
                    FileOutputStream fos = new FileOutputStream(destinationFilePath);
                    BufferedOutputStream dest = new BufferedOutputStream(fos,bufferSize);
                    while ((count = archiveInputStream.read(data, 0, bufferSize)) != -1) {
                        dest.write(data, 0, count);
                    }

                    dest.close();

                }

            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;

    }

    public void removeContainer(String containerId) {
        dockerClient.removeContainerCmd(containerId).exec();
    }

}
