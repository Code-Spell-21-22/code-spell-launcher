package pt.ua.deti.codespell.codespelllauncher.response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import pt.ua.deti.codespell.codespelllauncher.code.results.status.CodeExecutionStatus;

import java.util.List;
import java.util.UUID;

@Data
public class CodeLauncherResponse {

    private final UUID codeUniqueId;
    private final CodeExecutionStatus codeExecutionStatus;
    private final List<Object> observations;

    public String toJson() throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(this);
    }

}
