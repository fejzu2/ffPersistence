package pl.fejzu.persistence.sync;

public enum SyncReason {
    JOIN,
    QUIT,
    TRANSFER,
    SECTOR_CHANGE,
    MANUAL,
    SCHEDULED,
    ON_CHANGE,
    RECONNECT,
    PULL_REQUEST
}
