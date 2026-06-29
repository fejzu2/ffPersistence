package pl.fejzu.persistence.sync;

public enum SyncStrategy {
    INSTANT,
    DEBOUNCE,
    BATCH,
    MANUAL,
    ON_QUIT,
    DISABLED
}
