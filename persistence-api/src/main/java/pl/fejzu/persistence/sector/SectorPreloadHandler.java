package pl.fejzu.persistence.sector;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface SectorPreloadHandler {

    CompletableFuture<Void> preload(UUID playerId, SectorId targetSector);

    CompletableFuture<Boolean> awaitReady(UUID playerId, long timeoutMs);

    void notifyReady(UUID playerId);
}
