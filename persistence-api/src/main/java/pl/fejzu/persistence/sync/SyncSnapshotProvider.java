package pl.fejzu.persistence.sync;

import java.util.Set;
import java.util.UUID;

public interface SyncSnapshotProvider {

    SyncSnapshot snapshot(Object entity, UUID entityId, String entityType);

    SyncSnapshot snapshotFields(Object entity, UUID entityId, String entityType, Set<String> fieldNames);
}
