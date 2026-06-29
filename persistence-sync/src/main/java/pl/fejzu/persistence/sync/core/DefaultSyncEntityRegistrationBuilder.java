package pl.fejzu.persistence.sync.core;

import pl.fejzu.persistence.sync.SyncDirection;
import pl.fejzu.persistence.sync.SyncEntity;
import pl.fejzu.persistence.sync.SyncEntityRegistrationBuilder;
import pl.fejzu.persistence.sync.SyncField;
import pl.fejzu.persistence.sync.SyncScope;
import pl.fejzu.persistence.sync.SyncStrategy;
import pl.fejzu.persistence.sync.annotation.SynchronizedField;

import java.util.ArrayList;
import java.util.List;

public final class DefaultSyncEntityRegistrationBuilder implements SyncEntityRegistrationBuilder {

    private final Class<?> entityClass;
    private final DefaultSyncRegistry registry;
    private final List<SyncField> fields = new ArrayList<>();
    private SyncScope scope = SyncScope.SERVER;
    private SyncStrategy defaultStrategy = SyncStrategy.INSTANT;
    private SyncDirection direction = SyncDirection.BIDIRECTIONAL;

    DefaultSyncEntityRegistrationBuilder(Class<?> entityClass, DefaultSyncRegistry registry) {
        this.entityClass = entityClass;
        this.registry = registry;
        addAnnotatedFields();
    }

    private void addAnnotatedFields() {
        for (java.lang.reflect.Field f : getAllFields(entityClass)) {
            SynchronizedField ann = f.getAnnotation(SynchronizedField.class);
            if (ann != null && !f.isAnnotationPresent(pl.fejzu.persistence.sync.annotation.SyncIgnore.class)) {
                fields.add(new SyncField(f.getName(), ann.strategy(), ann.delayMs(), ann.scope()));
            }
        }
    }

    private List<java.lang.reflect.Field> getAllFields(Class<?> clazz) {
        List<java.lang.reflect.Field> result = new ArrayList<>();
        Class<?> current = clazz;
        while (current != null && current != Object.class) {
            for (java.lang.reflect.Field f : current.getDeclaredFields()) result.add(f);
            current = current.getSuperclass();
        }
        return result;
    }

    @Override
    public SyncEntityRegistrationBuilder scope(SyncScope scope) {
        this.scope = scope;
        return this;
    }

    @Override
    public SyncEntityRegistrationBuilder defaultStrategy(SyncStrategy strategy) {
        this.defaultStrategy = strategy;
        return this;
    }

    @Override
    public SyncEntityRegistrationBuilder direction(SyncDirection direction) {
        this.direction = direction;
        return this;
    }

    @Override
    public SyncEntityRegistrationBuilder field(String name, SyncStrategy strategy) {
        fields.add(new SyncField(name, strategy, 0L, scope));
        return this;
    }

    @Override
    public SyncEntityRegistrationBuilder field(String name, SyncStrategy strategy, long delayMs) {
        fields.add(new SyncField(name, strategy, delayMs, scope));
        return this;
    }

    @Override
    public SyncEntityRegistrationBuilder field(String name, SyncStrategy strategy, long delayMs, SyncScope fieldScope) {
        fields.add(new SyncField(name, strategy, delayMs, fieldScope));
        return this;
    }

    @Override
    public SyncEntity register() {
        SyncEntity entity = new DefaultSyncEntity(entityClass, fields, scope, defaultStrategy, direction);
        registry.registerEntity(entity);
        return entity;
    }
}
