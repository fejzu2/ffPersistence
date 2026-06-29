package pl.fejzu.persistence.result;

public enum SaveReason {
    PLAYER_QUIT,
    PLAYER_TRANSFER,
    EXPLICIT_REQUEST,
    PERIODIC_SAVE,
    SHUTDOWN,
    DIRTY_FLUSH
}
