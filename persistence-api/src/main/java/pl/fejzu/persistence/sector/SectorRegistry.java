package pl.fejzu.persistence.sector;

import java.util.Collection;
import java.util.Optional;

public interface SectorRegistry {

    void register(Sector sector);

    void unregister(SectorId id);

    Optional<Sector> getSector(SectorId id);

    Collection<Sector> getAllSectors();

    Collection<Sector> getOnlineSectors();

    void updateState(SectorId id, SectorState state);
}
