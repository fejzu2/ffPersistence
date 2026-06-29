package pl.fejzu.persistence.core.repository;

import pl.fejzu.persistence.cache.CacheProvider;
import pl.fejzu.persistence.core.tracking.DirtyTracker;
import pl.fejzu.persistence.entity.PersistenceEntity;
import pl.fejzu.persistence.repository.PersistenceRepository;
import pl.fejzu.persistence.result.DataState;
import pl.fejzu.persistence.result.DeleteResult;
import pl.fejzu.persistence.result.LoadResult;
import pl.fejzu.persistence.result.SaveResult;
import pl.fejzu.persistence.service.PersistenceContext;
import pl.fejzu.persistence.service.PersistenceKey;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public abstract class AbstractPersistenceRepository<T extends PersistenceEntity<ID>, ID>
    implements PersistenceRepository<T, ID> {

    protected final PersistenceContext context;
    protected final Class<T> entityClass;
    protected final DirtyTracker<ID> dirtyTracker = new DirtyTracker<>();

    protected AbstractPersistenceRepository(PersistenceContext context, Class<T> entityClass) {
        this.context = context;
        this.entityClass = entityClass;
    }

    protected abstract CompletableFuture<Optional<T>> loadFromStorage(ID id);

    protected abstract CompletableFuture<Void> saveToStorage(T entity);

    protected abstract CompletableFuture<Void> deleteFromStorage(ID id);

    protected abstract CompletableFuture<Boolean> existsInStorage(ID id);

    protected abstract CompletableFuture<List<T>> findAllFromStorage();

    @Override
    public CompletableFuture<LoadResult<T>> load(ID id) {
        PersistenceKey key = PersistenceKey.of(entityClass, id);
        CacheProvider cache = context.getCacheProvider();

        Optional<T> cached = cache.get(key);
        if (cached.isPresent()) {
            return CompletableFuture.completedFuture(LoadResult.cached(cached.get()));
        }

        return loadFromStorage(id).<LoadResult<T>>thenApply(opt -> {
            if (opt.isEmpty()) {
                return LoadResult.notFound();
            }
            T entity = opt.get();
            cache.put(key, entity);
            return LoadResult.success(entity, DataState.LOADED);
        }).exceptionally(e -> LoadResult.error(e));
    }

    @Override
    public CompletableFuture<SaveResult> save(T entity) {
        PersistenceKey key = PersistenceKey.of(entityClass, entity.getId());

        return saveToStorage(entity).thenApply(v -> {
            context.getCacheProvider().put(key, entity);
            dirtyTracker.markClean(entity.getId());
            return SaveResult.success();
        }).exceptionally(SaveResult::error);
    }

    @Override
    public CompletableFuture<DeleteResult> delete(ID id) {
        PersistenceKey key = PersistenceKey.of(entityClass, id);

        return deleteFromStorage(id).thenApply(v -> {
            context.getCacheProvider().invalidate(key);
            dirtyTracker.markClean(id);
            return DeleteResult.success();
        }).exceptionally(DeleteResult::error);
    }

    @Override
    public CompletableFuture<Boolean> exists(ID id) {
        PersistenceKey key = PersistenceKey.of(entityClass, id);
        if (context.getCacheProvider().contains(key)) {
            return CompletableFuture.completedFuture(true);
        }
        return existsInStorage(id);
    }

    @Override
    public CompletableFuture<List<T>> findAll() {
        return findAllFromStorage();
    }

    @Override
    public CompletableFuture<Void> saveAll(List<T> entities) {
        List<CompletableFuture<SaveResult>> futures = entities.stream()
            .map(this::save)
            .toList();
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }

    public void markDirty(T entity) {
        dirtyTracker.markDirty(entity.getId());
    }

    public boolean isDirty(ID id) {
        return dirtyTracker.isDirty(id);
    }

    public Optional<T> getCached(ID id) {
        return context.getCacheProvider().get(PersistenceKey.of(entityClass, id));
    }

    public void invalidateCache(ID id) {
        context.getCacheProvider().invalidate(PersistenceKey.of(entityClass, id));
    }

    protected Executor getExecutor() {
        return context.getExecutor();
    }
}
