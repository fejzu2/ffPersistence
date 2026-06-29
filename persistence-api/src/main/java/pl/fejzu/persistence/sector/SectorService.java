package pl.fejzu.persistence.sector;

import java.util.Optional;
import java.util.UUID;

public interface SectorService {

    void registerSector(Sector sector);

    Optional<Sector> getSector(SectorId id);

    SectorRegistry registry();

    SectorPlayerTracker playerTracker();

    SectorLockService lockService();

    SectorTransfer transfer(UUID playerId);

    void start();

    void stop();

    boolean isRunning();
}
