package pl.fejzu.persistence.core.player;

import pl.fejzu.persistence.player.PlayerDataSession;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class PlayerDataSessionManager implements SessionManager {

    private final Map<UUID, DefaultPlayerDataSession> sessions = new ConcurrentHashMap<>();

    @Override
    public PlayerDataSession createSession(UUID playerId, String playerName) {
        DefaultPlayerDataSession session = new DefaultPlayerDataSession(playerId, playerName);
        sessions.put(playerId, session);
        return session;
    }

    @Override
    public Optional<PlayerDataSession> getSession(UUID playerId) {
        return Optional.ofNullable(sessions.get(playerId));
    }

    @Override
    public void removeSession(UUID playerId) {
        sessions.remove(playerId);
    }

    @Override
    public boolean hasSession(UUID playerId) {
        return sessions.containsKey(playerId);
    }

    @Override
    public Collection<PlayerDataSession> getActiveSessions() {
        return sessions.values().stream()
            .filter(PlayerDataSession::isActive)
            .map(s -> (PlayerDataSession) s)
            .toList();
    }

    @Override
    public int sessionCount() {
        return sessions.size();
    }
}
