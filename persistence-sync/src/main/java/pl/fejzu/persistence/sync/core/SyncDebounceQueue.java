package pl.fejzu.persistence.sync.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public final class SyncDebounceQueue {

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r, "sync-debounce");
        t.setDaemon(true);
        return t;
    });
    private final Map<String, ScheduledFuture<?>> pending = new ConcurrentHashMap<>();

    public void submit(String key, long delayMs, Runnable task) {
        ScheduledFuture<?> existing = pending.remove(key);
        if (existing != null && !existing.isDone()) {
            existing.cancel(false);
        }
        ScheduledFuture<?> future = scheduler.schedule(() -> {
            pending.remove(key);
            task.run();
        }, delayMs, TimeUnit.MILLISECONDS);
        pending.put(key, future);
    }

    public void cancel(String key) {
        ScheduledFuture<?> future = pending.remove(key);
        if (future != null) {
            future.cancel(false);
        }
    }

    public void shutdown() {
        scheduler.shutdown();
    }
}
