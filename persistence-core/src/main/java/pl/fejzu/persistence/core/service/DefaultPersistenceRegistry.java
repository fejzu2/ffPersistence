package pl.fejzu.persistence.core.service;

import pl.fejzu.persistence.core.repository.RepositoryRegistry;
import pl.fejzu.persistence.entity.PersistenceEntity;
import pl.fejzu.persistence.repository.PersistenceRepository;
import pl.fejzu.persistence.service.PersistenceContext;
import pl.fejzu.persistence.service.PersistenceRegistry;

public final class DefaultPersistenceRegistry implements PersistenceRegistry {

    private final RepositoryRegistry delegate = new RepositoryRegistry();

    @Override
    public <T extends PersistenceEntity<ID>, ID, R extends PersistenceRepository<T, ID>>
    void register(Class<R> repositoryClass, R repository) {
        delegate.register(repositoryClass, repository);
    }

    @Override
    public <T extends PersistenceEntity<ID>, ID, R extends PersistenceRepository<T, ID>>
    R getOrCreate(Class<R> repositoryClass, PersistenceContext context) {
        return delegate.getOrCreate(repositoryClass, context);
    }

    @Override
    public <T extends PersistenceEntity<ID>, ID, R extends PersistenceRepository<T, ID>>
    R get(Class<R> repositoryClass) {
        return delegate.get(repositoryClass);
    }

    @Override
    public boolean isRegistered(Class<?> repositoryClass) {
        return delegate.isRegistered(repositoryClass);
    }

    @Override
    public void clear() {
        delegate.clear();
    }
}
