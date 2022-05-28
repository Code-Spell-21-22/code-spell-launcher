package pt.ua.deti.codespell.codespelllauncher.code.report;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class Step {

    private int id;
    private boolean successful;
    private List<Object> args;

}
