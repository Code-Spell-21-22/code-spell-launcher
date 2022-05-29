package pt.ua.deti.codespell.codespelllauncher.code.results.entities.implementation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pt.ua.deti.codespell.codespelllauncher.code.results.entities.CodeResultEntity;
import pt.ua.deti.codespell.codespelllauncher.code.results.status.CodeExecutionStatus;
import pt.ua.deti.codespell.codespelllauncher.code.results.status.ExecutionStatus;
import pt.ua.deti.codespell.codespelllauncher.response.CodeLauncherResponse;

import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CodeExecutionResultEntity extends CodeResultEntity {

    private UUID codeUniqueId;
    private ExecutionStatus executionStatus;
    private long time;
    private int score;

    @Override
    public CodeLauncherResponse toCodeLauncherResponse() {
        return null;
    }

}
