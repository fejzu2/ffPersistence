package pl.fejzu.persistence.mongodb.repository;

import dev.morphia.query.filters.Filter;
import pl.fejzu.persistence.entity.PersistenceEntity;
import pl.fejzu.persistence.repository.PersistenceRepository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface MongoRepository<T extends PersistenceEntity<ID>, ID> extends PersistenceRepository<T, ID> {

    CompletableFuture<List<T>> findBy(Filter filter);

    CompletableFuture<Optional<T>> findOneBy(Filter filter);

    CompletableFuture<Long> count();
}
