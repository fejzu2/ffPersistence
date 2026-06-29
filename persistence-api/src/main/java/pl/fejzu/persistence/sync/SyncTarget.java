package pl.fejzu.persistence.sync;

import lombok.Value;

@Value
public class SyncTarget {

    String serverName;
    boolean broadcast;

    public static SyncTarget of(String serverName) {
        return new SyncTarget(serverName, false);
    }

    public static SyncTarget broadcast() {
        return new SyncTarget("*", true);
    }
}
