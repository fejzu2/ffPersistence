package pl.fejzu.persistence.sector;

public enum SectorTransferState {
    IDLE,
    LOCKING,
    SAVING,
    SYNCING,
    NOTIFYING,
    PRELOADING,
    TRANSFERRING,
    CONFIRMING,
    COMPLETED,
    FAILED,
    CANCELLED
}
