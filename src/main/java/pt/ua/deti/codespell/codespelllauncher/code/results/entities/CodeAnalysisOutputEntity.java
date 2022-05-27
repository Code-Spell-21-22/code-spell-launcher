package pt.ua.deti.codespell.codespelllauncher.code.results.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pt.ua.deti.codespell.codespelllauncher.code.results.status.AnalysisStatus;
import pt.ua.deti.codespell.codespelllauncher.code.results.status.CodeExecutionStatus;
import pt.ua.deti.codespell.codespelllauncher.response.CodeLauncherResponse;

import java.util.Collections;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CodeAnalysisOutputEntity extends CodeResultEntity {

    private UUID codeUniqueId;
    private AnalysisStatus analysisStatus;
    private long time;

    @Override
    public CodeLauncherResponse toCodeLauncherResponse() {
        return null;
    }

}
