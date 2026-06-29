package pl.fejzu.persistence.entity;

import java.util.UUID;

public interface PlayerPersistenceEntity extends PersistenceEntity<UUID> {

    UUID getUniqueId();

    String getName();

    @Override
    default UUID getId() {
        return getUniqueId();
    }
}
