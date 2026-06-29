package pl.fejzu.persistence.cache;

import pl.fejzu.persistence.service.PersistenceKey;

import java.util.Optional;

public interface CacheProvider {

    <T> Optional<T> get(PersistenceKey key);

    void put(PersistenceKey key, Object entity);

    void invalidate(PersistenceKey key);

    boolean contains(PersistenceKey key);

    void invalidateAll();

    int size();
}
