package pt.ua.deti.codespell.codespelllauncher.code.results.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pt.ua.deti.codespell.codespelllauncher.code.results.status.CodeExecutionStatus;
import pt.ua.deti.codespell.codespelllauncher.response.CodeLauncherResponse;

import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CodeExecutionResultEntity extends CodeResultEntity {

    private UUID codeUniqueId;
    private CodeExecutionStatus executionStatus;
    private long time;
    private int score;

    @Override
    public CodeLauncherResponse toCodeLauncherResponse() {
        return null;
    }

}
