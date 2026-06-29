package pl.fejzu.persistence.sector.core;

import pl.fejzu.persistence.sector.SectorId;
import pl.fejzu.persistence.sector.SectorLock;
import pl.fejzu.persistence.sector.SectorLockService;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class DefaultSectorLockService implements SectorLockService {

    private final Map<UUID, SectorLock> locks = new ConcurrentHashMap<>();

    @Override
    public boolean lock(UUID playerId, SectorId source, SectorId target, long timeoutMs) {
        SectorLock existing = locks.get(playerId);
        if (existing != null && !existing.isExpired()) {
            return false;
        }
        locks.put(playerId, new SectorLock(playerId, source, target, Instant.now(), timeoutMs));
        return true;
    }

    @Override
    public void unlock(UUID playerId) {
        locks.remove(playerId);
    }

    @Override
    public boolean isLocked(UUID playerId) {
        SectorLock lock = locks.get(playerId);
        if (lock == null) return false;
        if (lock.isExpired()) {
            locks.remove(playerId);
            return false;
        }
        return true;
    }

    @Override
    public Optional<SectorLock> getLock(UUID playerId) {
        SectorLock lock = locks.get(playerId);
        if (lock != null && lock.isExpired()) {
            locks.remove(playerId);
            return Optional.empty();
        }
        return Optional.ofNullable(lock);
    }
}
