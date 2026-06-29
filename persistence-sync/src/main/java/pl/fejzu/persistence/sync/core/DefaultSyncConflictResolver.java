package pl.fejzu.persistence.sync.core;

import pl.fejzu.persistence.sync.SyncConflictResolver;
import pl.fejzu.persistence.sync.SyncConflictStrategy;
import pl.fejzu.persistence.sync.SyncSnapshot;

import java.util.LinkedHashMap;
import java.util.Map;

public final class DefaultSyncConflictResolver implements SyncConflictResolver {

    private final SyncConflictStrategy defaultStrategy;

    public DefaultSyncConflictResolver(SyncConflictStrategy defaultStrategy) {
        this.defaultStrategy = defaultStrategy;
    }

    @Override
    public SyncSnapshot resolve(SyncSnapshot local, SyncSnapshot incoming, SyncConflictStrategy strategy) {
        SyncConflictStrategy active = strategy != null ? strategy : defaultStrategy;
        return switch (active) {
            case LAST_WRITE_WINS -> resolveLastWriteWins(local, incoming);
            case TIMESTAMP_CHECK -> resolveByTimestamp(local, incoming);
            case VERSION_CHECK -> resolveByVersion(local, incoming);
            case SOURCE_PRIORITY -> incoming;
            case CUSTOM -> incoming;
        };
    }

    private SyncSnapshot resolveLastWriteWins(SyncSnapshot local, SyncSnapshot incoming) {
        return incoming;
    }

    private SyncSnapshot resolveByTimestamp(SyncSnapshot local, SyncSnapshot incoming) {
        if (incoming.getTimestamp().isAfter(local.getTimestamp())) {
            return incoming;
        }
        return local;
    }

    private SyncSnapshot resolveByVersion(SyncSnapshot local, SyncSnapshot incoming) {
        if (incoming.getVersion() > local.getVersion()) {
            return incoming;
        }
        return local;
    }

    private SyncSnapshot mergeFields(SyncSnapshot local, SyncSnapshot incoming) {
        Map<String, Object> merged = new LinkedHashMap<>(local.getFields());
        merged.putAll(incoming.getFields());
        return DefaultSyncSnapshot.of(local.getEntityId(), local.getEntityType(), merged, Math.max(local.getVersion(), incoming.getVersion()));
    }
}
