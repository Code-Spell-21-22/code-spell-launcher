package pt.ua.deti.codespell.codespelllauncher.code.results.output;

import pt.ua.deti.codespell.codespelllauncher.code.report.CodeExecReport;
import pt.ua.deti.codespell.codespelllauncher.code.results.entities.*;
import pt.ua.deti.codespell.codespelllauncher.code.results.status.AnalysisStatus;
import pt.ua.deti.codespell.codespelllauncher.code.results.status.ExecutionStatus;
import pt.ua.deti.codespell.codespelllauncher.containers.ContainerLauncherManager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class OutputRegisterHandler {

    private final List<CodeExecutionOutput<?>> registeredCodeExecutionOutputs;
    private final ContainerLauncherManager containerLauncherManager;

    public OutputRegisterHandler(ContainerLauncherManager containerLauncherManager) {
        this.containerLauncherManager = containerLauncherManager;
        this.registeredCodeExecutionOutputs = new ArrayList<>();
    }

    public void registerOutput(CodeExecutionOutput<?> codeExecutionOutput) {
        registeredCodeExecutionOutputs.add(codeExecutionOutput);
    }

    public List<CodeExecutionOutput<?>> registeredOutputs() {
        return registeredCodeExecutionOutputs;
    }

    public void pullOutputFiles() {
        registeredCodeExecutionOutputs.forEach(codeExecutionOutput -> containerLauncherManager.pullData(codeExecutionOutput.getCodeExecutionInstance(), codeExecutionOutput));
    }

    public List<CodeResultEntity> getOutputEntities() {
        return registeredCodeExecutionOutputs.stream()
                .map(CodeExecutionOutput::toEntity)
                .collect(Collectors.toList());
    }

    public CodeExecReport createExecutionReport() {

        if (registeredCodeExecutionOutputs.isEmpty())
            return new CodeExecReport();

        CodeExecReport codeExecReport = new CodeExecReport();

        for (CodeResultEntity outputEntity : getOutputEntities()) {

            if (outputEntity instanceof CodeExecutionResultEntity) {
                codeExecReport.setId(((CodeExecutionResultEntity) outputEntity).getCodeUniqueId().toString());
                codeExecReport.setExecutionStatus(((CodeExecutionResultEntity) outputEntity).getExecutionStatus());
                codeExecReport.setScore(((CodeExecutionResultEntity) outputEntity).getScore());
            } else if (outputEntity instanceof CodeAnalysisOutputEntity) {
                codeExecReport.setAnalysisStatus(((CodeAnalysisOutputEntity) outputEntity).getAnalysisStatus());
            } else if (outputEntity instanceof ErrorsOutputEntity) {
                codeExecReport.setErrors(((ErrorsOutputEntity) outputEntity).getErrors());
            } else if (outputEntity instanceof ExecutionOutputEntity) {
                codeExecReport.setOutput(((ExecutionOutputEntity) outputEntity).getOutput());
            }

        }

        return codeExecReport;

    }

}
