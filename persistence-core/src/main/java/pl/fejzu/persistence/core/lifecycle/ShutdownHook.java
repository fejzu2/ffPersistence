package pl.fejzu.persistence.core.lifecycle;

public final class ShutdownHook {

    private final LifecycleService lifecycleService;
    private boolean registered = false;

    public ShutdownHook(LifecycleService lifecycleService) {
        this.lifecycleService = lifecycleService;
    }

    public void register() {
        if (registered) return;
        Runtime.getRuntime().addShutdownHook(new Thread(lifecycleService::shutdown, "persistence-shutdown-hook"));
        registered = true;
    }
}
