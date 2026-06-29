package pl.fejzu.persistence.sync;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface SyncEntity {

    Class<?> getEntityClass();

    List<SyncField> getFields();

    SyncScope getScope();

    SyncStrategy getDefaultStrategy();

    SyncDirection getDirection();

    Optional<SyncField> getField(String name);

    default Collection<SyncField> getFieldsForStrategy(SyncStrategy strategy) {
        return getFields().stream()
            .filter(f -> f.getStrategy() == strategy)
            .toList();
    }
}
