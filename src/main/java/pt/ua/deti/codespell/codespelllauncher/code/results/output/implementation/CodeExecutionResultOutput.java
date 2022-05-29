package pt.ua.deti.codespell.codespelllauncher.code.results.output.implementation;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import pt.ua.deti.codespell.codespelllauncher.code.results.entities.implementation.CodeExecutionResultEntity;
import pt.ua.deti.codespell.codespelllauncher.code.results.output.CodeExecutionOutput;
import pt.ua.deti.codespell.codespelllauncher.model.CodeExecutionInstance;

import java.io.File;
import java.io.IOException;

@Log4j2
public class CodeExecutionResultOutput extends CodeExecutionOutput<CodeExecutionResultEntity> {

    public CodeExecutionResultOutput(CodeExecutionInstance codeExecutionInstance) {
        super(codeExecutionInstance);
    }

    @Override
    public CodeExecutionResultEntity toEntity() {

        if (!getDestinationFile().exists())
            return null;

        try {
            return new ObjectMapper().readValue(getDestinationFile(), CodeExecutionResultEntity.class);
        } catch (IOException e) {
            log.warn(String.format("Error converting code execution output to entity for code %s.", codeExecutionInstance));
        }

        return null;

    }

    @Override
    public File getSourceFile() {
        return new File(File.separator + "results.txt");
    }

    @Override
    public File getDestinationFile() {
        return new File(getOutputDestinationDirectoryPath() + File.separator + "codeExecutionResult.txt");
    }

}
