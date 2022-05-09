package pt.ua.deti.codespell.codespelllauncher.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public class CodeExecutionInstance {

    private UUID codeUniqueId;
    private String code;

}
