package pl.fejzu.persistence.sector;

public enum SectorTransferFailReason {
    TIMEOUT,
    SECTOR_OFFLINE,
    SECTOR_NOT_FOUND,
    PLAYER_NOT_FOUND,
    LOCK_FAILED,
    SAVE_FAILED,
    SYNC_FAILED,
    PRELOAD_FAILED,
    TRANSFER_FAILED,
    NO_AVAILABLE_SECTOR,
    UNKNOWN
}
