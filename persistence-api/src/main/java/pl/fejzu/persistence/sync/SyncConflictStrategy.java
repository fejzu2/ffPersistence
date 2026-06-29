package pl.fejzu.persistence.sync;

public enum SyncConflictStrategy {
    LAST_WRITE_WINS,
    VERSION_CHECK,
    TIMESTAMP_CHECK,
    SOURCE_PRIORITY,
    CUSTOM
}
