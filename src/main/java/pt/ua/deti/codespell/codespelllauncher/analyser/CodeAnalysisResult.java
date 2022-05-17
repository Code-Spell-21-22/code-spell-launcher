package pt.ua.deti.codespell.codespelllauncher.analyser;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class CodeAnalysisResult {

    private final UUID codeUniqueId;
    private final AnalysisStatus analysisStatus;
    private final long time;

}
