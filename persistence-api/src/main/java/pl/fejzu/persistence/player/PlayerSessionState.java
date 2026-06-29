package pl.fejzu.persistence.player;

public enum PlayerSessionState {
    PENDING,
    LOADING,
    LOADED,
    SAVING,
    SAVED,
    UNLOADED,
    ERROR
}
