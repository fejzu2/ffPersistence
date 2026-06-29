package pl.fejzu.persistence.sync.core;

import pl.fejzu.persistence.sync.SyncEntity;
import pl.fejzu.persistence.sync.SyncField;
import pl.fejzu.persistence.sync.SyncSnapshot;
import pl.fejzu.persistence.sync.SyncSnapshotProvider;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class ReflectionSyncSnapshotProvider implements SyncSnapshotProvider {

    private final DefaultSyncRegistry registry;
    private final SyncFieldResolver fieldResolver = new SyncFieldResolver();

    public ReflectionSyncSnapshotProvider(DefaultSyncRegistry registry) {
        this.registry = registry;
    }

    @Override
    public SyncSnapshot snapshot(Object entity, UUID entityId, String entityType) {
        SyncEntity syncEntity = registry.getEntity(entity.getClass()).orElse(null);
        if (syncEntity == null) {
            return DefaultSyncSnapshot.empty(entityId, entityType);
        }
        Set<String> fieldNames = new java.util.LinkedHashSet<>();
        for (SyncField f : syncEntity.getFields()) {
            fieldNames.add(f.getName());
        }
        Map<String, Object> fields = fieldResolver.extractFields(entity, fieldNames);
        return DefaultSyncSnapshot.of(entityId, entityType, fields);
    }

    @Override
    public SyncSnapshot snapshotFields(Object entity, UUID entityId, String entityType, Set<String> fieldNames) {
        Map<String, Object> fields = fieldResolver.extractFields(entity, fieldNames);
        return DefaultSyncSnapshot.of(entityId, entityType, fields);
    }
}
