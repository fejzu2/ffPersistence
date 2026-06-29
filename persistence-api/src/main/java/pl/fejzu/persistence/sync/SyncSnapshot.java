package pl.fejzu.persistence.sync;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public interface SyncSnapshot {

    UUID getEntityId();

    String getEntityType();

    Map<String, Object> getFields();

    Instant getTimestamp();

    long getVersion();

    Map<String, Object> diff(SyncSnapshot other);
}
