package pt.ua.deti.codespell.codespelllauncher.containers;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Component
public class ContainerRegistry {

    private final Map<UUID, String> launchedContainers;

    public ContainerRegistry() {
        this.launchedContainers = new HashMap<>();
    }

    public void register(UUID codeUniqueId, String containerId) {
        launchedContainers.put(codeUniqueId, containerId);
    }

    public void unregister(UUID codeUniqueId) {
        launchedContainers.remove(codeUniqueId);
    }

    public boolean isRegistered(UUID codeUniqueId) {
        return launchedContainers.containsKey(codeUniqueId);
    }

    public String getRegistry(UUID codeUniqueId) {
        return launchedContainers.get(codeUniqueId);
    }

    public Set<UUID> getAllRegisters() {
        return launchedContainers.keySet();
    }

}
