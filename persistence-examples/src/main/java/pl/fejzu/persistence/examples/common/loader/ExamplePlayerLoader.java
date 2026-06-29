package pl.fejzu.persistence.examples.common.loader;

import pl.fejzu.persistence.examples.common.repository.ExampleUserRepository;
import pl.fejzu.persistence.player.PlayerDataLoader;
import pl.fejzu.persistence.result.LoadReason;
import pl.fejzu.persistence.result.SaveReason;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public final class ExamplePlayerLoader implements PlayerDataLoader {

    private final ExampleUserRepository userRepository;

    public ExamplePlayerLoader(ExampleUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public CompletableFuture<Void> loadPlayer(UUID playerId, String playerName, LoadReason reason) {
        return userRepository.loadOrCreate(playerId, playerName).thenApply(user -> null);
    }

    @Override
    public CompletableFuture<Void> savePlayer(UUID playerId, SaveReason reason) {
        return userRepository.getCached(playerId)
            .map(user -> userRepository.save(user).thenApply(v -> (Void) null))
            .orElseGet(() -> CompletableFuture.completedFuture(null));
    }

    @Override
    public CompletableFuture<Void> unloadPlayer(UUID playerId, SaveReason reason) {
        return userRepository.getCached(playerId)
            .map(user -> userRepository.save(user)
                .thenRun(() -> userRepository.invalidateCache(playerId)))
            .orElseGet(() -> CompletableFuture.completedFuture(null));
    }
}
