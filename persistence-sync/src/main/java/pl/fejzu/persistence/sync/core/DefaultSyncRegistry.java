package pl.fejzu.persistence.sync.core;

import pl.fejzu.persistence.sync.SyncEntity;
import pl.fejzu.persistence.sync.SyncEntityRegistrationBuilder;
import pl.fejzu.persistence.sync.SyncRegistry;
import pl.fejzu.persistence.sync.annotation.SynchronizedEntity;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class DefaultSyncRegistry implements SyncRegistry {

    private final Map<Class<?>, SyncEntity> entities = new ConcurrentHashMap<>();

    @Override
    public SyncEntityRegistrationBuilder entity(Class<?> entityClass) {
        return new DefaultSyncEntityRegistrationBuilder(entityClass, this);
    }

    @Override
    public Optional<SyncEntity> getEntity(Class<?> entityClass) {
        return Optional.ofNullable(entities.get(entityClass));
    }

    @Override
    public Collection<SyncEntity> getAllEntities() {
        return entities.values();
    }

    @Override
    public boolean isRegistered(Class<?> entityClass) {
        return entities.containsKey(entityClass);
    }

    void registerEntity(SyncEntity entity) {
        entities.put(entity.getEntityClass(), entity);
    }

    public void scanAndRegister(Class<?> entityClass) {
        SynchronizedEntity ann = SyncEntityScanner.findEntityAnnotation(entityClass);
        if (ann == null) return;
        entity(entityClass)
            .scope(SyncEntityScanner.resolveScope(entityClass))
            .defaultStrategy(ann.defaultStrategy())
            .direction(SyncEntityScanner.resolveDirection(entityClass))
            .register();
    }
}
