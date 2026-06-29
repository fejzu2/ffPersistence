package pl.fejzu.persistence.core.lifecycle;

import pl.fejzu.persistence.service.PersistenceService;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public final class LifecycleService {

    private static final Logger LOGGER = Logger.getLogger(LifecycleService.class.getName());

    private final List<PersistenceService> managedServices = new ArrayList<>();
    private boolean running = false;

    public void register(PersistenceService service) {
        managedServices.add(service);
    }

    public void start() {
        if (running) return;
        running = true;
        LOGGER.info("ffPersistence lifecycle started");
    }

    public void shutdown() {
        if (!running) return;
        running = false;
        LOGGER.info("ffPersistence shutting down...");
        managedServices.forEach(service -> {
            try {
                service.shutdown();
            } catch (Exception e) {
                LOGGER.warning("Error during service shutdown: " + e.getMessage());
            }
        });
        managedServices.clear();
        LOGGER.info("ffPersistence shutdown complete");
    }

    public boolean isRunning() {
        return running;
    }
}
