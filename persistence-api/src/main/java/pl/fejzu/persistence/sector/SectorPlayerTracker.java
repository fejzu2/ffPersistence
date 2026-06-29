package pl.fejzu.persistence.sector;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface SectorPlayerTracker {

    void track(UUID playerId, SectorId sectorId, String playerName);

    void untrack(UUID playerId);

    Optional<SectorId> getCurrentSector(UUID playerId);

    Optional<SectorId> getLastSector(UUID playerId);

    void updateLastSector(UUID playerId, SectorId sectorId);

    Collection<UUID> getPlayersInSector(SectorId sectorId);

    Optional<SectorPlayer> getPlayer(UUID playerId);
}
