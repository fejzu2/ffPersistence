package pl.fejzu.persistence.mongodb.repository;

import dev.morphia.Datastore;
import dev.morphia.query.filters.Filters;
import pl.fejzu.persistence.core.repository.AbstractPersistenceRepository;
import pl.fejzu.persistence.entity.PersistenceEntity;
import pl.fejzu.persistence.mongodb.MongoStorageProvider;
import pl.fejzu.persistence.service.PersistenceContext;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public abstract class AbstractMongoRepository<T extends PersistenceEntity<ID>, ID>
    extends AbstractPersistenceRepository<T, ID> {

    protected final Datastore datastore;

    protected AbstractMongoRepository(PersistenceContext context, Class<T> entityClass) {
        super(context, entityClass);
        this.datastore = ((MongoStorageProvider) context.getStorageProvider()).getDatastore();
    }

    @Override
    protected CompletableFuture<Optional<T>> loadFromStorage(ID id) {
        return CompletableFuture.supplyAsync(() -> {
            T entity = datastore.find(entityClass)
                .filter(Filters.eq("_id", id))
                .first();
            return Optional.ofNullable(entity);
        }, getExecutor());
    }

    @Override
    protected CompletableFuture<Void> saveToStorage(T entity) {
        return CompletableFuture.runAsync(() -> datastore.save(entity), getExecutor());
    }

    @Override
    protected CompletableFuture<Void> deleteFromStorage(ID id) {
        return CompletableFuture.runAsync(() ->
            datastore.find(entityClass)
                .filter(Filters.eq("_id", id))
                .delete(),
            getExecutor()
        );
    }

    @Override
    protected CompletableFuture<Boolean> existsInStorage(ID id) {
        return CompletableFuture.supplyAsync(() ->
            datastore.find(entityClass)
                .filter(Filters.eq("_id", id))
                .count() > 0,
            getExecutor()
        );
    }

    @Override
    protected CompletableFuture<List<T>> findAllFromStorage() {
        return CompletableFuture.supplyAsync(() ->
            datastore.find(entityClass).stream().toList(),
            getExecutor()
        );
    }

    public CompletableFuture<List<T>> findBy(dev.morphia.query.filters.Filter filter) {
        return CompletableFuture.supplyAsync(() ->
            datastore.find(entityClass).filter(filter).stream().toList(),
            getExecutor()
        );
    }

    public CompletableFuture<Optional<T>> findOneBy(dev.morphia.query.filters.Filter filter) {
        return CompletableFuture.supplyAsync(() ->
            Optional.ofNullable(datastore.find(entityClass).filter(filter).first()),
            getExecutor()
        );
    }

    public CompletableFuture<Long> count() {
        return CompletableFuture.supplyAsync(() ->
            datastore.find(entityClass).count(),
            getExecutor()
        );
    }
}
