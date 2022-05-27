package pt.ua.deti.codespell.codespelllauncher.code.results.output;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import pt.ua.deti.codespell.codespelllauncher.code.results.entities.CodeAnalysisOutputEntity;
import pt.ua.deti.codespell.codespelllauncher.model.CodeExecutionInstance;

import java.io.File;
import java.io.IOException;

@Log4j2
public class CodeAnalysisOutput extends CodeExecutionOutput<CodeAnalysisOutputEntity> {

    public CodeAnalysisOutput(CodeExecutionInstance codeExecutionInstance) {
        super(codeExecutionInstance);
    }

    @Override
    public CodeAnalysisOutputEntity toEntity() {

        if (!getDestinationFile().exists())
            return null;

        try {
            return new ObjectMapper().readValue(getDestinationFile(), CodeAnalysisOutputEntity.class);
        } catch (IOException e) {
            log.warn(String.format("Error converting code analysis output to entity for code %s.", codeExecutionInstance));
        }

        return null;

    }

    @Override
    public File getSourceFile() {
        return new File(File.separator + "analysis.txt");
    }

    @Override
    public File getDestinationFile() {
        return new File(getOutputDestinationDirectoryPath() + File.separator + "codeAnalysisResult.txt");
    }

}
