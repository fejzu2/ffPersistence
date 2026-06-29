package pl.fejzu.persistence.sector.core;

import pl.fejzu.persistence.sector.Sector;
import pl.fejzu.persistence.sector.SectorId;
import pl.fejzu.persistence.sector.SectorRegistry;
import pl.fejzu.persistence.sector.SectorState;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public final class DefaultSectorRegistry implements SectorRegistry {

    private final Map<SectorId, Sector> sectors = new ConcurrentHashMap<>();

    @Override
    public void register(Sector sector) {
        sectors.put(sector.getId(), sector);
    }

    @Override
    public void unregister(SectorId id) {
        sectors.remove(id);
    }

    @Override
    public Optional<Sector> getSector(SectorId id) {
        return Optional.ofNullable(sectors.get(id));
    }

    @Override
    public Collection<Sector> getAllSectors() {
        return sectors.values();
    }

    @Override
    public Collection<Sector> getOnlineSectors() {
        return sectors.values().stream()
            .filter(Sector::isOnline)
            .collect(Collectors.toList());
    }

    @Override
    public void updateState(SectorId id, SectorState state) {
        sectors.computeIfPresent(id, (k, sector) -> {
            if (sector instanceof DefaultSector ds) {
                return ds.withState(state);
            }
            return sector;
        });
    }
}
