package pl.fejzu.persistence.core.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public final class EntityMetadata {

    private final Class<?> entityClass;
    private final Class<?> idClass;
    private final String collectionName;
    private final boolean playerEntity;

    public static EntityMetadata of(Class<?> entityClass, Class<?> idClass, String collectionName) {
        return new EntityMetadata(entityClass, idClass, collectionName, false);
    }

    public static EntityMetadata playerEntity(Class<?> entityClass, String collectionName) {
        return new EntityMetadata(entityClass, java.util.UUID.class, collectionName, true);
    }
}
