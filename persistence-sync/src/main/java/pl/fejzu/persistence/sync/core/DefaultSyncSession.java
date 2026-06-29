package pl.fejzu.persistence.sync.core;

import lombok.Getter;
import pl.fejzu.persistence.sync.SyncReason;
import pl.fejzu.persistence.sync.SyncSession;
import pl.fejzu.persistence.sync.SyncState;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

@Getter
public final class DefaultSyncSession implements SyncSession {

    private final UUID sessionId = UUID.randomUUID();
    private final UUID entityId;
    private final String entityType;
    private final SyncReason reason;
    private final Instant startedAt = Instant.now();
    private final AtomicReference<SyncState> state = new AtomicReference<>(SyncState.PENDING);
    private volatile Instant completedAt;
    private final CompletableFuture<Void> future = new CompletableFuture<>();

    public DefaultSyncSession(UUID entityId, String entityType, SyncReason reason) {
        this.entityId = entityId;
        this.entityType = entityType;
        this.reason = reason;
    }

    @Override
    public SyncState getState() {
        return state.get();
    }

    @Override
    public Optional<Instant> getCompletedAt() {
        return Optional.ofNullable(completedAt);
    }

    @Override
    public CompletableFuture<Void> await() {
        return future;
    }

    public void start() {
        state.set(SyncState.IN_PROGRESS);
    }

    public void complete() {
        state.set(SyncState.COMPLETED);
        completedAt = Instant.now();
        future.complete(null);
    }

    public void fail(Throwable cause) {
        state.set(SyncState.FAILED);
        completedAt = Instant.now();
        future.completeExceptionally(cause);
    }

    public void cancel() {
        state.set(SyncState.CANCELLED);
        completedAt = Instant.now();
        future.cancel(true);
    }
}
