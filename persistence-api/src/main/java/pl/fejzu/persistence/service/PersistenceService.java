package pl.fejzu.persistence.service;

import pl.fejzu.persistence.entity.PersistenceEntity;
import pl.fejzu.persistence.repository.PersistenceRepository;

public interface PersistenceService {

    <T extends PersistenceEntity<ID>, ID, R extends PersistenceRepository<T, ID>>
    R repository(Class<R> repositoryClass);

    PersistenceContext getContext();

    void shutdown();
}
