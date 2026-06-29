package pl.fejzu.persistence.settings;

public enum SyncStrategy {
    INSTANT,
    DEBOUNCE,
    BATCH,
    MANUAL,
    ON_QUIT,
    DISABLED
}
