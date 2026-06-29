package pl.fejzu.persistence.core.cache;

import lombok.Getter;

import java.time.Instant;

@Getter
public final class CacheEntry<T> {

    private final T value;
    private final Instant createdAt;
    private volatile Instant lastAccessedAt;

    public CacheEntry(T value) {
        this.value = value;
        this.createdAt = Instant.now();
        this.lastAccessedAt = this.createdAt;
    }

    public T access() {
        lastAccessedAt = Instant.now();
        return value;
    }

    public boolean isExpiredByWrite(java.time.Duration ttl) {
        return Instant.now().isAfter(createdAt.plus(ttl));
    }

    public boolean isExpiredByAccess(java.time.Duration ttl) {
        return Instant.now().isAfter(lastAccessedAt.plus(ttl));
    }
}
