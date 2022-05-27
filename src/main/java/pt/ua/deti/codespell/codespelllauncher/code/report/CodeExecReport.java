package pt.ua.deti.codespell.codespelllauncher.code.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pt.ua.deti.codespell.codespelllauncher.code.results.status.AnalysisStatus;
import pt.ua.deti.codespell.codespelllauncher.code.results.status.ExecutionStatus;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CodeExecReport {

    private String id;
    private AnalysisStatus analysisStatus;
    private ExecutionStatus executionStatus;
    private List<Step> steps;
    private int score;
    private List<String> output;
    private List<String> errors;

}
