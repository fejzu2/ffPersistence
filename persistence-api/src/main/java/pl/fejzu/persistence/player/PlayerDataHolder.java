package pl.fejzu.persistence.player;

import pl.fejzu.persistence.entity.PlayerPersistenceEntity;

import java.util.Optional;
import java.util.UUID;

public interface PlayerDataHolder {

    <T extends PlayerPersistenceEntity> Optional<T> get(UUID playerId, Class<T> type);

    <T extends PlayerPersistenceEntity> void set(UUID playerId, T data);

    <T extends PlayerPersistenceEntity> void remove(UUID playerId, Class<T> type);

    boolean isLoaded(UUID playerId);

    void clear(UUID playerId);
}
