package pl.fejzu.persistence.sector;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface SectorSyncBridge {

    CompletableFuture<Void> syncPlayerToSector(UUID playerId, SectorId targetSector);

    CompletableFuture<Void> syncAllFields(UUID playerId, SectorId targetSector);
}
