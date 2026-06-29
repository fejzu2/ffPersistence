package pl.fejzu.persistence.sync;

import lombok.Value;

import java.util.UUID;

@Value
public class SyncSource {

    String serverName;
    UUID playerId;

    public static SyncSource of(String serverName) {
        return new SyncSource(serverName, null);
    }

    public static SyncSource player(UUID playerId, String serverName) {
        return new SyncSource(serverName, playerId);
    }
}
