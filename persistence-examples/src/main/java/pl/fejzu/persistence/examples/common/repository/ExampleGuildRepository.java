package pl.fejzu.persistence.examples.common.repository;

import dev.morphia.query.filters.Filters;
import pl.fejzu.persistence.examples.common.model.ExampleGuild;
import pl.fejzu.persistence.mongodb.repository.AbstractMongoRepository;
import pl.fejzu.persistence.service.PersistenceContext;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public final class ExampleGuildRepository extends AbstractMongoRepository<ExampleGuild, String> {

    public ExampleGuildRepository(PersistenceContext context) {
        super(context, ExampleGuild.class);
    }

    public CompletableFuture<Optional<ExampleGuild>> findByOwner(UUID owner) {
        return findOneBy(Filters.eq("owner", owner));
    }

    public CompletableFuture<Void> addMember(String guildId, UUID member) {
        return load(guildId).thenCompose(result -> {
            if (result.getData() == null) return CompletableFuture.completedFuture(null);
            result.getData().getMembers().add(member);
            return save(result.getData()).thenApply(v -> null);
        });
    }
}
