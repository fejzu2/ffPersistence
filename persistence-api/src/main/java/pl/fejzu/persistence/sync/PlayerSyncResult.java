package pl.fejzu.persistence.sync;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Value
@Builder
public class PlayerSyncResult {

    UUID playerId;
    boolean success;
    SyncState state;
    Set<String> syncedFields;
    Instant timestamp;
    String errorMessage;
    Throwable cause;

    public boolean isFailure() {
        return !success;
    }

    public static PlayerSyncResult success(UUID playerId, Set<String> syncedFields) {
        return PlayerSyncResult.builder()
            .playerId(playerId)
            .success(true)
            .state(SyncState.COMPLETED)
            .syncedFields(syncedFields)
            .timestamp(Instant.now())
            .build();
    }

    public static PlayerSyncResult failure(UUID playerId, String errorMessage, Throwable cause) {
        return PlayerSyncResult.builder()
            .playerId(playerId)
            .success(false)
            .state(SyncState.FAILED)
            .syncedFields(Set.of())
            .timestamp(Instant.now())
            .errorMessage(errorMessage)
            .cause(cause)
            .build();
    }
}
