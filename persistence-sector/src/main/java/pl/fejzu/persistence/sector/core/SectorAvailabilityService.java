package pl.fejzu.persistence.sector.core;

import pl.fejzu.persistence.sector.Sector;
import pl.fejzu.persistence.sector.SectorId;
import pl.fejzu.persistence.sector.SectorRegistry;

import java.util.Optional;

public final class SectorAvailabilityService {

    private final SectorRegistry registry;

    public SectorAvailabilityService(SectorRegistry registry) {
        this.registry = registry;
    }

    public boolean isAvailable(SectorId sectorId) {
        return registry.getSector(sectorId)
            .filter(Sector::isOnline)
            .isPresent();
    }

    public boolean isAvailableAndHasCapacity(SectorId sectorId) {
        return registry.getSector(sectorId)
            .filter(Sector::isOnline)
            .filter(s -> s.getMaxPlayers() < 0 || s.getMaxPlayers() > 0)
            .isPresent();
    }

    public Optional<Sector> findBestAvailable() {
        return registry.getOnlineSectors().stream()
            .filter(s -> s.getMaxPlayers() < 0 || s.getMaxPlayers() > 0)
            .max(java.util.Comparator.comparingInt(Sector::getPriority));
    }

    public Optional<Sector> findLowestOnline() {
        return registry.getOnlineSectors().stream()
            .min(java.util.Comparator.comparingInt(Sector::getPriority));
    }
}
