package pl.fejzu.persistence.core.queue;

import pl.fejzu.persistence.core.executor.PersistenceExecutor;
import pl.fejzu.persistence.core.failure.FailureHandler;
import pl.fejzu.persistence.entity.PersistenceEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;

public final class SaveQueue {

    private final BlockingQueue<SaveTask<?>> queue = new LinkedBlockingQueue<>();
    private final PersistenceExecutor executor;
    private final FailureHandler failureHandler;

    public SaveQueue(PersistenceExecutor executor, FailureHandler failureHandler) {
        this.executor = executor;
        this.failureHandler = failureHandler;
    }

    public <T extends PersistenceEntity<?>> void enqueue(SaveTask<T> task) {
        queue.offer(task);
    }

    public CompletableFuture<Void> flush() {
        List<SaveTask<?>> tasks = new ArrayList<>();
        queue.drainTo(tasks);

        if (tasks.isEmpty()) {
            return CompletableFuture.completedFuture(null);
        }

        List<CompletableFuture<Void>> futures = tasks.stream()
            .map(task -> executor.run(task::execute)
                .exceptionally(throwable -> {
                    failureHandler.onSaveFailure(task, throwable);
                    return null;
                }))
            .toList();

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }

    public int size() {
        return queue.size();
    }

    public void clear() {
        queue.clear();
    }
}
