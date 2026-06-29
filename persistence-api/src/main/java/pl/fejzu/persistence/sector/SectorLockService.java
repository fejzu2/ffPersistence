package pl.fejzu.persistence.sector;

import java.util.Optional;
import java.util.UUID;

public interface SectorLockService {

    boolean lock(UUID playerId, SectorId source, SectorId target, long timeoutMs);

    void unlock(UUID playerId);

    boolean isLocked(UUID playerId);

    Optional<SectorLock> getLock(UUID playerId);
}
