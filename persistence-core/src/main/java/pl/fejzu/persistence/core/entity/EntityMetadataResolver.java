package pl.fejzu.persistence.core.entity;

import pl.fejzu.persistence.entity.PlayerPersistenceEntity;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class EntityMetadataResolver {

    private final Map<Class<?>, EntityMetadata> metadataCache = new ConcurrentHashMap<>();

    public EntityMetadata resolve(Class<?> entityClass) {
        return metadataCache.computeIfAbsent(entityClass, this::buildMetadata);
    }

    public void register(Class<?> entityClass, EntityMetadata metadata) {
        metadataCache.put(entityClass, metadata);
    }

    public Optional<EntityMetadata> find(Class<?> entityClass) {
        return Optional.ofNullable(metadataCache.get(entityClass));
    }

    private EntityMetadata buildMetadata(Class<?> entityClass) {
        boolean isPlayerEntity = PlayerPersistenceEntity.class.isAssignableFrom(entityClass);
        String collectionName = entityClass.getSimpleName().toLowerCase() + "s";

        if (isPlayerEntity) {
            return EntityMetadata.playerEntity(entityClass, collectionName);
        }

        return EntityMetadata.of(entityClass, Object.class, collectionName);
    }
}
