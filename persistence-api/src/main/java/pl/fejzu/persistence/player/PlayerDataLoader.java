package pl.fejzu.persistence.player;

import pl.fejzu.persistence.result.LoadReason;
import pl.fejzu.persistence.result.SaveReason;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface PlayerDataLoader {

    CompletableFuture<Void> loadPlayer(UUID playerId, String playerName, LoadReason reason);

    CompletableFuture<Void> savePlayer(UUID playerId, SaveReason reason);

    CompletableFuture<Void> unloadPlayer(UUID playerId, SaveReason reason);
}
