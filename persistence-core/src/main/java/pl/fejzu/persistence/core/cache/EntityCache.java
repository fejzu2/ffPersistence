package pl.fejzu.persistence.core.cache;

import pl.fejzu.persistence.service.PersistenceKey;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class EntityCache {

    private final Map<PersistenceKey, CacheEntry<?>> entries = new ConcurrentHashMap<>();
    private final CachePolicy policy;

    public EntityCache(CachePolicy policy) {
        this.policy = policy;
    }

    public EntityCache() {
        this(CachePolicy.defaultPolicy());
    }

    @SuppressWarnings("unchecked")
    public <T> Optional<T> get(PersistenceKey key) {
        CacheEntry<?> entry = entries.get(key);
        if (entry == null) {
            return Optional.empty();
        }
        if (isExpired(entry)) {
            entries.remove(key);
            return Optional.empty();
        }
        return Optional.of((T) entry.access());
    }

    public void put(PersistenceKey key, Object value) {
        if (entries.size() >= policy.getMaxSize()) {
            evictOldest();
        }
        entries.put(key, new CacheEntry<>(value));
    }

    public void invalidate(PersistenceKey key) {
        entries.remove(key);
    }

    public void invalidateAll() {
        entries.clear();
    }

    public boolean contains(PersistenceKey key) {
        CacheEntry<?> entry = entries.get(key);
        if (entry == null) return false;
        if (isExpired(entry)) {
            entries.remove(key);
            return false;
        }
        return true;
    }

    public int size() {
        return entries.size();
    }

    private boolean isExpired(CacheEntry<?> entry) {
        return entry.isExpiredByWrite(policy.getExpireAfterWrite())
            || entry.isExpiredByAccess(policy.getExpireAfterAccess());
    }

    private void evictOldest() {
        entries.entrySet().stream()
            .min((a, b) -> a.getValue().getLastAccessedAt().compareTo(b.getValue().getLastAccessedAt()))
            .map(Map.Entry::getKey)
            .ifPresent(entries::remove);
    }
}
