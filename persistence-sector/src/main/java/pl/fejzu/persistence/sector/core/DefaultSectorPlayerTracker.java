package pl.fejzu.persistence.sector.core;

import pl.fejzu.persistence.sector.SectorId;
import pl.fejzu.persistence.sector.SectorPlayer;
import pl.fejzu.persistence.sector.SectorPlayerTracker;

import java.time.Instant;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public final class DefaultSectorPlayerTracker implements SectorPlayerTracker {

    private final Map<UUID, SectorPlayer> players = new ConcurrentHashMap<>();

    @Override
    public void track(UUID playerId, SectorId sectorId, String playerName) {
        SectorPlayer existing = players.get(playerId);
        SectorId lastSector = existing != null ? existing.getCurrentSector() : null;
        players.put(playerId, new SectorPlayer(playerId, playerName, sectorId, lastSector, Instant.now(), Instant.now()));
    }

    @Override
    public void untrack(UUID playerId) {
        players.remove(playerId);
    }

    @Override
    public Optional<SectorId> getCurrentSector(UUID playerId) {
        return Optional.ofNullable(players.get(playerId)).map(SectorPlayer::getCurrentSector);
    }

    @Override
    public Optional<SectorId> getLastSector(UUID playerId) {
        return Optional.ofNullable(players.get(playerId)).map(SectorPlayer::getLastSector);
    }

    @Override
    public void updateLastSector(UUID playerId, SectorId sectorId) {
        players.computeIfPresent(playerId, (id, player) ->
            new SectorPlayer(player.getPlayerId(), player.getPlayerName(), player.getCurrentSector(), sectorId, player.getLastSectorChange(), Instant.now())
        );
    }

    @Override
    public Collection<UUID> getPlayersInSector(SectorId sectorId) {
        return players.values().stream()
            .filter(p -> sectorId.equals(p.getCurrentSector()))
            .map(SectorPlayer::getPlayerId)
            .collect(Collectors.toList());
    }

    @Override
    public Optional<SectorPlayer> getPlayer(UUID playerId) {
        return Optional.ofNullable(players.get(playerId));
    }
}
