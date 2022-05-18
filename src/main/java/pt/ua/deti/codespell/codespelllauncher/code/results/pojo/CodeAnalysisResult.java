package pt.ua.deti.codespell.codespelllauncher.code.results.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import pt.ua.deti.codespell.codespelllauncher.code.results.status.CodeExecutionStatus;
import pt.ua.deti.codespell.codespelllauncher.response.CodeLauncherResponse;

import java.util.Collections;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CodeAnalysisResult implements ICodeResultPOJO {

    private UUID codeUniqueId;
    private CodeExecutionStatus analysisStatus;
    private long time;

    @Override
    public CodeLauncherResponse toCodeLauncherResponse() {
        return new CodeLauncherResponse(codeUniqueId, analysisStatus, Collections.emptyList());
    }

}
