package pl.fejzu.persistence.sync;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface SyncSession {

    UUID getSessionId();

    UUID getEntityId();

    String getEntityType();

    SyncState getState();

    SyncReason getReason();

    Instant getStartedAt();

    Optional<Instant> getCompletedAt();

    CompletableFuture<Void> await();
}
