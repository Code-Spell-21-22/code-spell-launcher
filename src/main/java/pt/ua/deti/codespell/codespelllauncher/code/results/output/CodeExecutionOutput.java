package pt.ua.deti.codespell.codespelllauncher.code.results.output;

import org.apache.commons.io.FileUtils;
import pt.ua.deti.codespell.codespelllauncher.model.CodeExecutionInstance;

import java.io.File;
import java.io.IOException;

public abstract class CodeExecutionOutput<T> {

    protected final CodeExecutionInstance codeExecutionInstance;

    public CodeExecutionOutput(CodeExecutionInstance codeExecutionInstance) {
        this.codeExecutionInstance = codeExecutionInstance;
    }

    public abstract T toEntity() throws IOException;

    public abstract File getSourceFile();

    public abstract File getDestinationFile();

    public String getOutputDestinationDirectoryPath() {
        return FileUtils.getUserDirectoryPath() + File.separator + "Code_Spell" + File.separator + "Launcher" + File.separator + "Output" + File.separator + codeExecutionInstance.getCodeUniqueId().toString();
    }

}
