package pl.fejzu.persistence.core.player;

import lombok.Getter;
import pl.fejzu.persistence.player.PlayerDataSession;
import pl.fejzu.persistence.player.PlayerSessionState;

import java.time.Instant;
import java.util.UUID;

@Getter
public final class DefaultPlayerDataSession implements PlayerDataSession {

    private final UUID playerId;
    private final String playerName;
    private final Instant loginTime;
    private volatile PlayerSessionState state;
    private volatile Instant loadedAt;
    private volatile Throwable lastError;

    public DefaultPlayerDataSession(UUID playerId, String playerName) {
        this.playerId = playerId;
        this.playerName = playerName;
        this.loginTime = Instant.now();
        this.state = PlayerSessionState.PENDING;
    }

    @Override
    public boolean isLoaded() {
        return state == PlayerSessionState.LOADED;
    }

    @Override
    public boolean isActive() {
        return state == PlayerSessionState.LOADED || state == PlayerSessionState.SAVING;
    }

    @Override
    public void markLoading() {
        state = PlayerSessionState.LOADING;
    }

    @Override
    public void markLoaded() {
        state = PlayerSessionState.LOADED;
        loadedAt = Instant.now();
    }

    @Override
    public void markSaving() {
        state = PlayerSessionState.SAVING;
    }

    @Override
    public void markSaved() {
        state = PlayerSessionState.SAVED;
    }

    @Override
    public void markError(Throwable cause) {
        state = PlayerSessionState.ERROR;
        lastError = cause;
    }
}
