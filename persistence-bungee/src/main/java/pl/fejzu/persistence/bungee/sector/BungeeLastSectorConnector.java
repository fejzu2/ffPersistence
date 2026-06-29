package pl.fejzu.persistence.bungee.sector;

import pl.fejzu.persistence.sector.LastSectorProvider;
import pl.fejzu.persistence.sector.SectorId;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public final class BungeeLastSectorConnector implements LastSectorProvider {

    private final ConcurrentHashMap<UUID, SectorId> lastSectors = new ConcurrentHashMap<>();

    @Override
    public CompletableFuture<Optional<SectorId>> getLastSector(UUID playerId) {
        return CompletableFuture.completedFuture(Optional.ofNullable(lastSectors.get(playerId)));
    }

    @Override
    public CompletableFuture<Void> saveLastSector(UUID playerId, SectorId sectorId) {
        lastSectors.put(playerId, sectorId);
        return CompletableFuture.completedFuture(null);
    }

    public void removeLastSector(UUID playerId) {
        lastSectors.remove(playerId);
    }
}
