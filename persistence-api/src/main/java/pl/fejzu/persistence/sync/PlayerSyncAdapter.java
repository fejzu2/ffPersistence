package pl.fejzu.persistence.sync;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface PlayerSyncAdapter {

    Optional<Object> getPlayerEntity(UUID playerId);

    CompletableFuture<Void> applySnapshot(UUID playerId, SyncSnapshot snapshot);
}
