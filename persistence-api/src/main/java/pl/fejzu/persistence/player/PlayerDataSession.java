package pl.fejzu.persistence.player;

import java.time.Instant;
import java.util.UUID;

public interface PlayerDataSession {

    UUID getPlayerId();

    String getPlayerName();

    PlayerSessionState getState();

    Instant getLoginTime();

    Instant getLoadedAt();

    boolean isLoaded();

    boolean isActive();

    void markLoading();

    void markLoaded();

    void markSaving();

    void markSaved();

    void markError(Throwable cause);
}
