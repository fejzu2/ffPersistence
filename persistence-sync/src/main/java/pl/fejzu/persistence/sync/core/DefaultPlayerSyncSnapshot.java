package pl.fejzu.persistence.sync.core;

import lombok.Getter;
import pl.fejzu.persistence.sync.PlayerSyncSnapshot;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Getter
public final class DefaultPlayerSyncSnapshot implements PlayerSyncSnapshot {

    private final UUID entityId;
    private final String entityType;
    private final Map<String, Object> fields;
    private final Instant timestamp;
    private final long version;
    private final String playerName;
    private final String lastServer;

    public DefaultPlayerSyncSnapshot(UUID playerId, String playerName, String lastServer, Map<String, Object> fields, long version) {
        this.entityId = playerId;
        this.entityType = "player";
        this.playerName = playerName;
        this.lastServer = lastServer;
        this.fields = Map.copyOf(fields);
        this.timestamp = Instant.now();
        this.version = version;
    }

    public static DefaultPlayerSyncSnapshot of(UUID playerId, String playerName, String lastServer, Map<String, Object> fields) {
        return new DefaultPlayerSyncSnapshot(playerId, playerName, lastServer, fields, 0L);
    }

    @Override
    public Map<String, Object> diff(pl.fejzu.persistence.sync.SyncSnapshot other) {
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
