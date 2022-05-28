package pt.ua.deti.codespell.codespelllauncher.code.results.output.implementation;

import lombok.extern.log4j.Log4j2;
import pt.ua.deti.codespell.codespelllauncher.code.results.entities.implementation.ErrorsOutputEntity;
import pt.ua.deti.codespell.codespelllauncher.code.results.output.CodeExecutionOutput;
import pt.ua.deti.codespell.codespelllauncher.model.CodeExecutionInstance;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

@Log4j2
public class ErrorsOutput extends CodeExecutionOutput<ErrorsOutputEntity> {

    public ErrorsOutput(CodeExecutionInstance codeExecutionInstance) {
        super(codeExecutionInstance);
    }

    @Override
    public ErrorsOutputEntity toEntity() {

        List<String> allLines = new ArrayList<>();

        if (getSourceFile().exists()) {

            try {
                allLines = Files.readAllLines(getDestinationFile().toPath());
            } catch (IOException ioException) {
                log.warn("Error parsing errors output to logical entity.");
            }

        }

        return new ErrorsOutputEntity(allLines);

    }

    @Override
    public File getSourceFile() {
        return new File(File.separator + "errors.txt");
    }

    @Override
    public File getDestinationFile() {
        return new File(getOutputDestinationDirectoryPath() + File.separator + "errors.txt");
    }

}
