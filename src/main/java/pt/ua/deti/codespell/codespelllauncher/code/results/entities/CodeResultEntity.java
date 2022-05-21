package pt.ua.deti.codespell.codespelllauncher.code.results.entities;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import pt.ua.deti.codespell.codespelllauncher.response.CodeLauncherResponse;

@Log4j2
public abstract class CodeResultEntity {

    public abstract CodeLauncherResponse toCodeLauncherResponse();

    public String toJsonString() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            log.warn("Unable to convert CodeResultEntity to Json string.");
        }
        return null;
    }

}
