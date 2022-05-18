package pt.ua.deti.codespell.codespelllauncher.code.results;

import pt.ua.deti.codespell.codespelllauncher.model.CodeExecutionInstance;

import java.io.File;
import java.io.IOException;

public abstract class CodeExecutionResult<T> {

    protected final CodeExecutionInstance codeExecutionInstance;

    public CodeExecutionResult(CodeExecutionInstance codeExecutionInstance) {
        this.codeExecutionInstance = codeExecutionInstance;
    }

    public abstract T toEntity() throws IOException;

    public abstract File getSourceFile();

    public abstract File getDestinationFile();

}
