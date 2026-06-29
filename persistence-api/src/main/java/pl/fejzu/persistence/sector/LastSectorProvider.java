package pl.fejzu.persistence.sector;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface LastSectorProvider {

    CompletableFuture<Optional<SectorId>> getLastSector(UUID playerId);

    CompletableFuture<Void> saveLastSector(UUID playerId, SectorId sectorId);
}
