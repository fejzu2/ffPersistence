package pl.fejzu.persistence.examples.common.repository;

import pl.fejzu.persistence.examples.common.model.ExampleUser;
import pl.fejzu.persistence.mongodb.repository.AbstractMongoRepository;
import pl.fejzu.persistence.service.PersistenceContext;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public final class ExampleUserRepository extends AbstractMongoRepository<ExampleUser, UUID> {

    public ExampleUserRepository(PersistenceContext context) {
        super(context, ExampleUser.class);
    }

    public CompletableFuture<ExampleUser> loadOrCreate(UUID id, String name) {
        return load(id).thenCompose(result -> {
            if (result.getData() != null) {
                return CompletableFuture.completedFuture(result.getData());
            }
            ExampleUser user = ExampleUser.builder()
                .uniqueId(id)
                .name(name)
                .coins(0L)
                .level(1)
                .rank("default")
                .build();
            return save(user).thenApply(saveResult -> user);
        });
    }

    public CompletableFuture<Void> addCoins(UUID id, long amount) {
        Optional<ExampleUser> cached = getCached(id);
        if (cached.isPresent()) {
            cached.get().setCoins(cached.get().getCoins() + amount);
            return save(cached.get()).thenApply(v -> null);
        }
        return load(id).thenCompose(result -> {
            if (result.getData() == null) return CompletableFuture.completedFuture(null);
            result.getData().setCoins(result.getData().getCoins() + amount);
            return save(result.getData()).thenApply(v -> null);
        });
    }

    public CompletableFuture<Void> setLastSector(UUID id, String sector) {
        Optional<ExampleUser> cached = getCached(id);
        if (cached.isPresent()) {
            cached.get().setLastSector(sector);
            return save(cached.get()).thenApply(v -> null);
        }
        return load(id).thenCompose(result -> {
            if (result.getData() == null) return CompletableFuture.completedFuture(null);
            result.getData().setLastSector(sector);
            return save(result.getData()).thenApply(v -> null);
        });
    }

    public CompletableFuture<Void> setRank(UUID id, String rank) {
        Optional<ExampleUser> cached = getCached(id);
        if (cached.isPresent()) {
            cached.get().setRank(rank);
            return save(cached.get()).thenApply(v -> null);
        }
        return load(id).thenCompose(result -> {
            if (result.getData() == null) return CompletableFuture.completedFuture(null);
            result.getData().setRank(rank);
            return save(result.getData()).thenApply(v -> null);
        });
    }
}
