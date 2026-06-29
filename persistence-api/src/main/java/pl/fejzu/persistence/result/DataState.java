package pl.fejzu.persistence.result;

public enum DataState {
    LOADED,
    CACHED,
    NOT_FOUND,
    ERROR,
    DIRTY,
    CLEAN,
    PENDING_SAVE,
    PENDING_LOAD
}
