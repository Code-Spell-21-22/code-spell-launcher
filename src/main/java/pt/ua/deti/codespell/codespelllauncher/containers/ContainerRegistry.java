package pt.ua.deti.codespell.codespelllauncher.containers;

import org.springframework.stereotype.Component;
import pt.ua.deti.codespell.codespelllauncher.model.CodeExecutionInstance;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
public class ContainerRegistry {

    private final Map<CodeExecutionInstance, String> launchedContainers;

    public ContainerRegistry() {
        this.launchedContainers = new HashMap<>();
    }

    public void register(CodeExecutionInstance codeExecutionInstance, String containerId) {
        launchedContainers.put(codeExecutionInstance, containerId);
    }

    public void unregister(CodeExecutionInstance codeExecutionInstance) {
        launchedContainers.remove(codeExecutionInstance);
    }

    public boolean isRegistered(CodeExecutionInstance codeExecutionInstance) {
        return launchedContainers.containsKey(codeExecutionInstance);
    }

    public String getRegistry(CodeExecutionInstance codeExecutionInstance) {
        return launchedContainers.get(codeExecutionInstance);
    }

    public Set<CodeExecutionInstance> getAllRegisters() {
        return launchedContainers.keySet();
    }

}
