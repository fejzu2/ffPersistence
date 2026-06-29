package pl.fejzu.persistence.sync.core;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

public final class SyncExecutor {

    private final Executor executor;

    public SyncExecutor() {
        this.executor = Executors.newFixedThreadPool(
            Math.max(2, Runtime.getRuntime().availableProcessors()),
            r -> {
                Thread t = new Thread(r, "sync-worker");
                t.setDaemon(true);
                return t;
            }
        );
    }

    public SyncExecutor(Executor executor) {
        this.executor = executor;
    }

    public <T> CompletableFuture<T> submit(Supplier<T> task) {
        return CompletableFuture.supplyAsync(task, executor);
    }

    public CompletableFuture<Void> run(Runnable task) {
        return CompletableFuture.runAsync(task, executor);
    }

    public Executor getExecutor() {
        return executor;
    }
}
