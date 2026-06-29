package pl.fejzu.persistence.core.player;

import pl.fejzu.persistence.player.PlayerDataSession;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface SessionManager {

    PlayerDataSession createSession(UUID playerId, String playerName);

    Optional<PlayerDataSession> getSession(UUID playerId);

    void removeSession(UUID playerId);

    boolean hasSession(UUID playerId);

    Collection<PlayerDataSession> getActiveSessions();

    int sessionCount();
}
