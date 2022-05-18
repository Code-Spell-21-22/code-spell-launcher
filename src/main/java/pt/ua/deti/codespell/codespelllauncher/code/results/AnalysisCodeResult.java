package pt.ua.deti.codespell.codespelllauncher.code.results;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import pt.ua.deti.codespell.codespelllauncher.code.results.pojo.CodeAnalysisResult;
import pt.ua.deti.codespell.codespelllauncher.model.CodeExecutionInstance;

import java.io.File;
import java.io.IOException;

public class AnalysisCodeResult extends CodeExecutionResult<CodeAnalysisResult> {

    public AnalysisCodeResult(CodeExecutionInstance codeExecutionInstance) {
        super(codeExecutionInstance);
    }

    @Override
    public CodeAnalysisResult toEntity() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(getDestinationFile(), CodeAnalysisResult.class);
    }

    @Override
    public File getSourceFile() {
        return new File(File.separator + "analysis.txt");
    }

    @Override
    public File getDestinationFile() {
        return new File(FileUtils.getUserDirectoryPath() + File.separator + "Code_Spell" + File.separator + "Launcher" + File.separator + "Output" + File.separator + codeExecutionInstance.getCodeUniqueId().toString() + File.separator + "analysis.txt");
    }

}
