package pl.fejzu.persistence.sync;

import java.util.Collection;
import java.util.Optional;

public interface SyncRegistry {

    SyncEntityRegistrationBuilder entity(Class<?> entityClass);

    Optional<SyncEntity> getEntity(Class<?> entityClass);

    Collection<SyncEntity> getAllEntities();

    boolean isRegistered(Class<?> entityClass);
}
