package pl.fejzu.persistence.sync.core;

import pl.fejzu.persistence.sync.PlayerSyncResult;
import pl.fejzu.persistence.sync.PlayerSyncService;
import pl.fejzu.persistence.sync.SyncConflictResolver;
import pl.fejzu.persistence.sync.SyncPacket;
import pl.fejzu.persistence.sync.SyncReason;
import pl.fejzu.persistence.sync.SyncService;
import pl.fejzu.persistence.sync.SyncSnapshotProvider;
import pl.fejzu.persistence.sync.SyncState;
import pl.fejzu.persistence.sync.SyncTarget;
import pl.fejzu.persistence.settings.SyncSettings;
import pl.fejzu.persistence.sync.packet.PlayerFieldSyncPacket;
import pl.fejzu.persistence.sync.packet.PlayerFullSyncPacket;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public final class DefaultPlayerSyncService implements PlayerSyncService {

    private final SyncService syncService;
    private final SyncSnapshotProvider snapshotProvider;
    private final SyncConflictResolver conflictResolver;
    private final SyncSettings settings;

    public DefaultPlayerSyncService(SyncService syncService, SyncSnapshotProvider snapshotProvider, SyncConflictResolver conflictResolver, SyncSettings settings) {
        this.syncService = syncService;
        this.snapshotProvider = snapshotProvider;
        this.conflictResolver = conflictResolver;
        this.settings = settings;
    }

    @Override
    public CompletableFuture<PlayerSyncResult> syncFull(UUID playerId) {
        return push(playerId, null);
    }

    @Override
    public CompletableFuture<PlayerSyncResult> syncField(UUID playerId, String field) {
        return syncFields(playerId, Set.of(field));
    }

    @Override
    public CompletableFuture<PlayerSyncResult> syncFields(UUID playerId, Set<String> fields) {
        PlayerFieldSyncPacket packet = PlayerFieldSyncPacket.builder()
            .playerId(playerId)
            .fieldNames(fields)
            .reason(SyncReason.ON_CHANGE)
            .build();
        return syncService.broadcast(packet)
            .thenApply(v -> PlayerSyncResult.success(playerId, fields))
            .exceptionally(e -> PlayerSyncResult.failure(playerId, e.getMessage(), e));
    }

    @Override
    public CompletableFuture<PlayerSyncResult> broadcast(UUID playerId) {
        PlayerFullSyncPacket packet = PlayerFullSyncPacket.builder()
            .playerId(playerId)
            .reason(SyncReason.MANUAL)
            .build();
        return syncService.broadcast(packet)
            .thenApply(v -> PlayerSyncResult.success(playerId, Set.of()))
            .exceptionally(e -> PlayerSyncResult.failure(playerId, e.getMessage(), e));
    }

    @Override
    public CompletableFuture<PlayerSyncResult> pull(UUID playerId, String fromServer) {
        PlayerFullSyncPacket request = PlayerFullSyncPacket.builder()
            .playerId(playerId)
            .targetServer(fromServer)
            .reason(SyncReason.PULL_REQUEST)
            .build();
        return syncService.send(request, SyncTarget.of(fromServer))
            .thenApply(v -> PlayerSyncResult.builder()
                .playerId(playerId)
                .success(true)
                .state(SyncState.COMPLETED)
                .syncedFields(Set.of())
                .build())
            .exceptionally(e -> PlayerSyncResult.failure(playerId, e.getMessage(), e));
    }

    @Override
    public CompletableFuture<PlayerSyncResult> push(UUID playerId, String toServer) {
        PlayerFullSyncPacket packet = PlayerFullSyncPacket.builder()
            .playerId(playerId)
            .targetServer(toServer)
            .reason(SyncReason.MANUAL)
            .build();
        SyncTarget target = toServer != null ? SyncTarget.of(toServer) : SyncTarget.broadcast();
        return syncService.send(packet, target)
            .thenApply(v -> PlayerSyncResult.success(playerId, Set.of()))
            .exceptionally(e -> PlayerSyncResult.failure(playerId, e.getMessage(), e));
    }
}
