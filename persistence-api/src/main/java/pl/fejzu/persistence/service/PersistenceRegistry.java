package pl.fejzu.persistence.service;

import pl.fejzu.persistence.entity.PersistenceEntity;
import pl.fejzu.persistence.repository.PersistenceRepository;

public interface PersistenceRegistry {

    <T extends PersistenceEntity<ID>, ID, R extends PersistenceRepository<T, ID>>
    void register(Class<R> repositoryClass, R repository);

    <T extends PersistenceEntity<ID>, ID, R extends PersistenceRepository<T, ID>>
    R getOrCreate(Class<R> repositoryClass, PersistenceContext context);

    <T extends PersistenceEntity<ID>, ID, R extends PersistenceRepository<T, ID>>
    R get(Class<R> repositoryClass);

    boolean isRegistered(Class<?> repositoryClass);

    void clear();
}
