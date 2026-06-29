package pl.fejzu.persistence.sector;

import lombok.Value;

import java.time.Instant;
import java.util.UUID;

@Value
public class SectorLock {

    UUID playerId;
    SectorId sourceSector;
    SectorId targetSector;
    Instant lockedAt;
    long timeoutMs;

    public boolean isExpired() {
        return Instant.now().isAfter(lockedAt.plusMillis(timeoutMs));
    }
}
