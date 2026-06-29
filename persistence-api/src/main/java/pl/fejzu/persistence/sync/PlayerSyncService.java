package pl.fejzu.persistence.sync;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface PlayerSyncService {

    CompletableFuture<PlayerSyncResult> syncFull(UUID playerId);

    CompletableFuture<PlayerSyncResult> syncField(UUID playerId, String field);

    CompletableFuture<PlayerSyncResult> syncFields(UUID playerId, Set<String> fields);

    CompletableFuture<PlayerSyncResult> broadcast(UUID playerId);

    CompletableFuture<PlayerSyncResult> pull(UUID playerId, String fromServer);

    CompletableFuture<PlayerSyncResult> push(UUID playerId, String toServer);
}
