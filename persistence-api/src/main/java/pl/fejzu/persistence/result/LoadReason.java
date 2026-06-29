package pl.fejzu.persistence.result;

public enum LoadReason {
    PLAYER_JOIN,
    PLAYER_TRANSFER,
    EXPLICIT_REQUEST,
    PREFETCH,
    RELOAD,
    PROXY_REQUEST
}
