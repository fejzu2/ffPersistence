package pl.fejzu.persistence.core.cache;

import pl.fejzu.persistence.cache.CacheProvider;
import pl.fejzu.persistence.service.PersistenceKey;

import java.util.Optional;

public final class MemoryCacheProvider implements CacheProvider {

    private final EntityCache cache;

    public MemoryCacheProvider() {
        this(CachePolicy.defaultPolicy());
    }

    public MemoryCacheProvider(CachePolicy policy) {
        this.cache = new EntityCache(policy);
    }

    @Override
    public <T> Optional<T> get(PersistenceKey key) {
        return cache.get(key);
    }

    @Override
    public void put(PersistenceKey key, Object entity) {
        cache.put(key, entity);
    }

    @Override
    public void invalidate(PersistenceKey key) {
        cache.invalidate(key);
    }

    @Override
    public boolean contains(PersistenceKey key) {
        return cache.contains(key);
    }

    @Override
    public void invalidateAll() {
        cache.invalidateAll();
    }

    @Override
    public int size() {
        return cache.size();
    }
}
