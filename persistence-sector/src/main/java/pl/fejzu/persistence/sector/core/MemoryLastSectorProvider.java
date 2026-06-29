package pl.fejzu.persistence.sector.core;

import pl.fejzu.persistence.sector.LastSectorProvider;
import pl.fejzu.persistence.sector.SectorId;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public final class MemoryLastSectorProvider implements LastSectorProvider {

    private final Map<UUID, SectorId> lastSectors = new ConcurrentHashMap<>();

    @Override
    public CompletableFuture<Optional<SectorId>> getLastSector(UUID playerId) {
        return CompletableFuture.completedFuture(Optional.ofNullable(lastSectors.get(playerId)));
    }

    @Override
    public CompletableFuture<Void> saveLastSector(UUID playerId, SectorId sectorId) {
        if (sectorId != null) {
            lastSectors.put(playerId, sectorId);
        } else {
            lastSectors.remove(playerId);
        }
        return CompletableFuture.completedFuture(null);
    }
}
