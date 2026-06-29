package pl.fejzu.persistence.sector.core;

import pl.fejzu.persistence.sector.SectorId;
import pl.fejzu.persistence.sector.SectorSyncBridge;
import pl.fejzu.persistence.sync.PlayerSyncService;
import pl.fejzu.persistence.sync.SyncService;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public final class DefaultSectorSyncBridge implements SectorSyncBridge {

    private final SyncService syncService;

    public DefaultSectorSyncBridge(SyncService syncService) {
        this.syncService = syncService;
    }

    @Override
    public CompletableFuture<Void> syncPlayerToSector(UUID playerId, SectorId targetSector) {
        PlayerSyncService playerSync = syncService.playerSync();
        return playerSync.broadcast(playerId).thenAccept(result -> {});
    }

    @Override
    public CompletableFuture<Void> syncAllFields(UUID playerId, SectorId targetSector) {
        return syncPlayerToSector(playerId, targetSector);
    }
}
