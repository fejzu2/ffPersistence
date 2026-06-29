package pl.fejzu.persistence.sync.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public final class SyncBatchQueue {

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r, "sync-batch");
        t.setDaemon(true);
        return t;
    });
    private final Map<String, List<Runnable>> batches = new ConcurrentHashMap<>();
    private final Consumer<List<Runnable>> batchProcessor;

    public SyncBatchQueue(long intervalMs, Consumer<List<Runnable>> batchProcessor) {
        this.batchProcessor = batchProcessor;
        scheduler.scheduleAtFixedRate(this::flush, intervalMs, intervalMs, TimeUnit.MILLISECONDS);
    }

    public void submit(String batchKey, Runnable task) {
        batches.computeIfAbsent(batchKey, k -> new ArrayList<>()).add(task);
    }

    private void flush() {
        for (Map.Entry<String, List<Runnable>> entry : batches.entrySet()) {
            List<Runnable> tasks = batches.remove(entry.getKey());
            if (tasks != null && !tasks.isEmpty()) {
                batchProcessor.accept(tasks);
            }
        }
    }

    public void shutdown() {
        flush();
        scheduler.shutdown();
    }
}
