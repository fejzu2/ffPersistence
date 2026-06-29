package pl.fejzu.persistence.sector.core;

import pl.fejzu.persistence.sector.SectorId;
import pl.fejzu.persistence.sector.SectorPreloadHandler;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public final class SectorPlayerDataPreloader {

    private final SectorPreloadHandler preloadHandler;

    public SectorPlayerDataPreloader(SectorPreloadHandler preloadHandler) {
        this.preloadHandler = preloadHandler;
    }

    public CompletableFuture<Boolean> preloadAndWait(UUID playerId, SectorId targetSector, long timeoutMs) {
        return preloadHandler.preload(playerId, targetSector)
            .thenCompose(v -> preloadHandler.awaitReady(playerId, timeoutMs))
            .exceptionally(e -> false);
    }

    public void notifyReady(UUID playerId) {
        preloadHandler.notifyReady(playerId);
    }
}
