package pl.fejzu.persistence.sync.core;

import lombok.Getter;
import pl.fejzu.persistence.sync.SyncDirection;
import pl.fejzu.persistence.sync.SyncEntity;
import pl.fejzu.persistence.sync.SyncField;
import pl.fejzu.persistence.sync.SyncScope;
import pl.fejzu.persistence.sync.SyncStrategy;

import java.util.List;
import java.util.Optional;

@Getter
public final class DefaultSyncEntity implements SyncEntity {

    private final Class<?> entityClass;
    private final List<SyncField> fields;
    private final SyncScope scope;
    private final SyncStrategy defaultStrategy;
    private final SyncDirection direction;

    public DefaultSyncEntity(Class<?> entityClass, List<SyncField> fields, SyncScope scope, SyncStrategy defaultStrategy, SyncDirection direction) {
        this.entityClass = entityClass;
        this.fields = List.copyOf(fields);
        this.scope = scope;
        this.defaultStrategy = defaultStrategy;
        this.direction = direction;
    }

    @Override
    public Optional<SyncField> getField(String name) {
        return fields.stream().filter(f -> f.getName().equals(name)).findFirst();
    }
}
