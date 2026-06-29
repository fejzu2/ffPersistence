package pl.fejzu.persistence.sector.core;

import pl.fejzu.persistence.sector.LastSectorProvider;
import pl.fejzu.persistence.sector.LastSectorResolver;
import pl.fejzu.persistence.sector.Sector;
import pl.fejzu.persistence.sector.SectorId;
import pl.fejzu.persistence.sector.SectorRegistry;
import pl.fejzu.persistence.settings.SectorSettings;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public final class DefaultLastSectorResolver implements LastSectorResolver {

    private final LastSectorProvider provider;
    private final SectorRegistry registry;
    private final SectorSettings settings;

    public DefaultLastSectorResolver(LastSectorProvider provider, SectorRegistry registry, SectorSettings settings) {
        this.provider = provider;
        this.registry = registry;
        this.settings = settings;
    }

    @Override
    public CompletableFuture<Optional<SectorId>> resolve(UUID playerId) {
        return provider.getLastSector(playerId).thenApply(lastSectorOpt -> {
            if (lastSectorOpt.isPresent()) {
                SectorId lastSectorId = lastSectorOpt.get();
                if (!settings.isValidateLastSectorOnline()) {
                    return Optional.of(lastSectorId);
                }
                Optional<Sector> sector = registry.getSector(lastSectorId);
                if (sector.isPresent() && sector.get().isOnline()) {
                    return Optional.of(lastSectorId);
                }
            }
            if (settings.isAllowFallbackWhenLastOffline()) {
                return resolveFallback();
            }
            return Optional.empty();
        });
    }

    private Optional<SectorId> resolveFallback() {
        String fallback = settings.getFallbackSector();
        if (fallback != null && !fallback.isBlank()) {
            SectorId fallbackId = SectorId.of(fallback);
            Optional<Sector> fallbackSector = registry.getSector(fallbackId);
            if (fallbackSector.isPresent() && fallbackSector.get().isOnline()) {
                return Optional.of(fallbackId);
            }
        }
        String lobby = settings.getLobbySector();
        if (lobby != null && !lobby.isBlank()) {
            SectorId lobbyId = SectorId.of(lobby);
            Optional<Sector> lobbySector = registry.getSector(lobbyId);
            if (lobbySector.isPresent() && lobbySector.get().isOnline()) {
                return Optional.of(lobbyId);
            }
        }
        return Optional.empty();
    }
}
