package pl.fejzu.persistence.core.executor;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public final class PersistenceExecutor {

    private final ExecutorService executor;

    public PersistenceExecutor() {
        this(Runtime.getRuntime().availableProcessors() * 2);
    }

    public PersistenceExecutor(int threadPoolSize) {
        this.executor = Executors.newFixedThreadPool(
            threadPoolSize,
            new PersistenceThreadFactory("persistence-worker")
        );
    }

    public <T> CompletableFuture<T> supply(Supplier<T> supplier) {
        return CompletableFuture.supplyAsync(supplier, executor);
    }

    public CompletableFuture<Void> run(Runnable runnable) {
        return CompletableFuture.runAsync(runnable, executor);
    }

    public ExecutorService getExecutorService() {
        return executor;
    }

    public void shutdown() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
