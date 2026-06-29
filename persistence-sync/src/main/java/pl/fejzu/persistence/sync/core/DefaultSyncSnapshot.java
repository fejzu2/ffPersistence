package pl.fejzu.persistence.sync.core;

import lombok.Getter;
import pl.fejzu.persistence.sync.SyncSnapshot;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Getter
public final class DefaultSyncSnapshot implements SyncSnapshot {

    private final UUID entityId;
    private final String entityType;
    private final Map<String, Object> fields;
    private final Instant timestamp;
    private final long version;

    private DefaultSyncSnapshot(UUID entityId, String entityType, Map<String, Object> fields, Instant timestamp, long version) {
        this.entityId = entityId;
        this.entityType = entityType;
        this.fields = Map.copyOf(fields);
        this.timestamp = timestamp;
        this.version = version;
    }

    public static DefaultSyncSnapshot of(UUID entityId, String entityType, Map<String, Object> fields) {
        return new DefaultSyncSnapshot(entityId, entityType, fields, Instant.now(), 0L);
    }

    public static DefaultSyncSnapshot of(UUID entityId, String entityType, Map<String, Object> fields, long version) {
        return new DefaultSyncSnapshot(entityId, entityType, fields, Instant.now(), version);
    }

    public static DefaultSyncSnapshot empty(UUID entityId, String entityType) {
        return of(entityId, entityType, Map.of());
    }

    @Override
    public Map<String, Object> diff(SyncSnapshot other) {
        Map<String, Object> diffs = new LinkedHashMap<>();
        for (Map.Entry<String, Object> entry : fields.entrySet()) {
            Object otherVal = other.getFields().get(entry.getKey());
            if (!Objects.equals(entry.getValue(), otherVal)) {
                diffs.put(entry.getKey(), entry.getValue());
            }
        }
        return diffs;
    }
}
