package pt.ua.deti.codespell.codespelllauncher.code.results.entities.implementation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pt.ua.deti.codespell.codespelllauncher.code.results.entities.CodeResultEntity;
import pt.ua.deti.codespell.codespelllauncher.response.CodeLauncherResponse;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ExecutionOutputEntity extends CodeResultEntity {

    private List<String> output;

    @Override
    public CodeLauncherResponse toCodeLauncherResponse() {
        return null;
    }

}
