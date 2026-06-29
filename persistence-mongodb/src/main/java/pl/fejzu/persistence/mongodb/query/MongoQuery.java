package pl.fejzu.persistence.mongodb.query;

import dev.morphia.Datastore;
import dev.morphia.query.Query;
import dev.morphia.query.filters.Filter;
import dev.morphia.query.filters.Filters;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public final class MongoQuery<T> {

    private final Class<T> entityClass;
    private final Datastore datastore;
    private final Executor executor;

    public MongoQuery(Class<T> entityClass, Datastore datastore, Executor executor) {
        this.entityClass = entityClass;
        this.datastore = datastore;
        this.executor = executor;
    }

    public CompletableFuture<Optional<T>> findById(Object id) {
        return CompletableFuture.supplyAsync(() ->
            Optional.ofNullable(datastore.find(entityClass).filter(Filters.eq("_id", id)).first()),
            executor
        );
    }

    public CompletableFuture<List<T>> findAll() {
        return CompletableFuture.supplyAsync(() ->
            datastore.find(entityClass).stream().toList(),
            executor
        );
    }

    public CompletableFuture<List<T>> findBy(Filter filter) {
        return CompletableFuture.supplyAsync(() ->
            datastore.find(entityClass).filter(filter).stream().toList(),
            executor
        );
    }

    @SuppressWarnings("deprecation")
    public CompletableFuture<List<T>> findBy(Filter filter, int limit, int skip) {
        return CompletableFuture.supplyAsync(() ->
            datastore.find(entityClass).filter(filter)
                .stream(new dev.morphia.query.FindOptions().limit(limit).skip(skip))
                .toList(),
            executor
        );
    }

    public CompletableFuture<Long> count() {
        return CompletableFuture.supplyAsync(() ->
            datastore.find(entityClass).count(),
            executor
        );
    }

    public Query<T> raw() {
        return datastore.find(entityClass);
    }
}
