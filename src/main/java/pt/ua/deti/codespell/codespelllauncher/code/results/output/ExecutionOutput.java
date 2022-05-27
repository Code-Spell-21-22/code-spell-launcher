package pt.ua.deti.codespell.codespelllauncher.code.results.output;

import lombok.extern.log4j.Log4j2;
import pt.ua.deti.codespell.codespelllauncher.code.results.entities.ExecutionOutputEntity;
import pt.ua.deti.codespell.codespelllauncher.model.CodeExecutionInstance;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

@Log4j2
public class ExecutionOutput extends CodeExecutionOutput<ExecutionOutputEntity> {

    public ExecutionOutput(CodeExecutionInstance codeExecutionInstance) {
        super(codeExecutionInstance);
    }

    @Override
    public ExecutionOutputEntity toEntity() {

        List<String> allLines = new ArrayList<>();

        if (getDestinationFile().exists()) {
            try {
                allLines = Files.readAllLines(getDestinationFile().toPath());
            } catch (IOException ioException) {
                log.warn("Error parsing errors output to logical entity.");
            }
        }

        return new ExecutionOutputEntity(allLines);


    }

    @Override
    public File getSourceFile() {
        return new File(File.separator + "output.txt");
    }

    @Override
    public File getDestinationFile() {
        return new File(getOutputDestinationDirectoryPath() + File.separator + "output.txt");
    }
}
