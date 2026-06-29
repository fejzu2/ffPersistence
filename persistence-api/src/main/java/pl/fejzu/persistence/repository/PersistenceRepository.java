package pl.fejzu.persistence.repository;

import pl.fejzu.persistence.entity.PersistenceEntity;
import pl.fejzu.persistence.result.DeleteResult;
import pl.fejzu.persistence.result.LoadResult;
import pl.fejzu.persistence.result.SaveResult;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface PersistenceRepository<T extends PersistenceEntity<ID>, ID> {

    CompletableFuture<LoadResult<T>> load(ID id);

    CompletableFuture<SaveResult> save(T entity);

    CompletableFuture<DeleteResult> delete(ID id);

    CompletableFuture<Boolean> exists(ID id);

    CompletableFuture<List<T>> findAll();

    CompletableFuture<Void> saveAll(List<T> entities);
}
