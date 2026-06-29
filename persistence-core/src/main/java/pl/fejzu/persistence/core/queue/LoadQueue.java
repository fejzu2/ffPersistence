package pl.fejzu.persistence.core.queue;

import pl.fejzu.persistence.core.executor.PersistenceExecutor;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public final class LoadQueue {

    private final Map<Object, CompletableFuture<?>> pendingLoads = new ConcurrentHashMap<>();
    private final PersistenceExecutor executor;

    public LoadQueue(PersistenceExecutor executor) {
        this.executor = executor;
    }

    @SuppressWarnings("unchecked")
    public <T> CompletableFuture<T> getOrLoad(Object key, java.util.function.Supplier<CompletableFuture<T>> loader) {
        return (CompletableFuture<T>) pendingLoads.computeIfAbsent(key, k -> {
            CompletableFuture<T> future = loader.get();
            future.whenComplete((result, error) -> pendingLoads.remove(k));
            return future;
        });
    }

    public boolean isPending(Object key) {
        return pendingLoads.containsKey(key);
    }

    public int pendingCount() {
        return pendingLoads.size();
    }
}
