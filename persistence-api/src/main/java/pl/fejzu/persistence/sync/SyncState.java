package pl.fejzu.persistence.sync;

public enum SyncState {
    IDLE,
    PENDING,
    IN_PROGRESS,
    COMPLETED,
    FAILED,
    CANCELLED
}
