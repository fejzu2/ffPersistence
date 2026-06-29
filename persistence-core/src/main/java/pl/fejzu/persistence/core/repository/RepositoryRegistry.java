package pl.fejzu.persistence.core.repository;

import pl.fejzu.persistence.entity.PersistenceEntity;
import pl.fejzu.persistence.repository.PersistenceRepository;
import pl.fejzu.persistence.service.PersistenceContext;
import pl.fejzu.persistence.service.PersistenceRegistry;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class RepositoryRegistry implements PersistenceRegistry {

    private final Map<Class<?>, PersistenceRepository<?, ?>> repositories = new ConcurrentHashMap<>();

    @Override
    @SuppressWarnings("unchecked")
    public <T extends PersistenceEntity<ID>, ID, R extends PersistenceRepository<T, ID>>
    void register(Class<R> repositoryClass, R repository) {
        repositories.put(repositoryClass, repository);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends PersistenceEntity<ID>, ID, R extends PersistenceRepository<T, ID>>
    R getOrCreate(Class<R> repositoryClass, PersistenceContext context) {
        PersistenceRepository<?, ?> existing = repositories.get(repositoryClass);
        if (existing != null) {
            return (R) existing;
        }
        R created = instantiate(repositoryClass, context);
        repositories.put(repositoryClass, created);
        return created;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends PersistenceEntity<ID>, ID, R extends PersistenceRepository<T, ID>>
    R get(Class<R> repositoryClass) {
        R repo = (R) repositories.get(repositoryClass);
        if (repo == null) {
            throw new IllegalStateException("Repository not registered: " + repositoryClass.getName());
        }
        return repo;
    }

    @Override
    public boolean isRegistered(Class<?> repositoryClass) {
        return repositories.containsKey(repositoryClass);
    }

    @Override
    public void clear() {
        repositories.clear();
    }

    @SuppressWarnings("unchecked")
    private <R> R instantiate(Class<R> repositoryClass, PersistenceContext context) {
        try {
            Constructor<R> constructor = repositoryClass.getConstructor(PersistenceContext.class);
            return constructor.newInstance(context);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(
                "Repository " + repositoryClass.getName() + " must have a constructor accepting PersistenceContext", e
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to instantiate repository: " + repositoryClass.getName(), e);
        }
    }
}
