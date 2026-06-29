package pl.fejzu.persistence.core.repository;

import pl.fejzu.persistence.entity.PlayerPersistenceEntity;
import pl.fejzu.persistence.result.LoadResult;
import pl.fejzu.persistence.result.SaveResult;
import pl.fejzu.persistence.service.PersistenceContext;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public abstract class AbstractPlayerRepository<T extends PlayerPersistenceEntity>
    extends AbstractPersistenceRepository<T, UUID> {

    protected AbstractPlayerRepository(PersistenceContext context, Class<T> entityClass) {
        super(context, entityClass);
    }

    public CompletableFuture<LoadResult<T>> loadPlayer(UUID playerId) {
        return load(playerId);
    }

    public CompletableFuture<SaveResult> savePlayer(T entity) {
        return save(entity);
    }

    public CompletableFuture<Void> saveAndEvict(T entity) {
        return save(entity).thenRun(() -> invalidateCache(entity.getUniqueId()));
    }
}
