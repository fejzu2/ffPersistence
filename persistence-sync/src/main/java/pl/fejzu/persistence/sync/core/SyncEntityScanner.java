package pl.fejzu.persistence.sync.core;

import pl.fejzu.persistence.sync.SyncDirection;
import pl.fejzu.persistence.sync.SyncField;
import pl.fejzu.persistence.sync.SyncScope;
import pl.fejzu.persistence.sync.annotation.SyncIgnore;
import pl.fejzu.persistence.sync.annotation.SynchronizedEntity;
import pl.fejzu.persistence.sync.annotation.SynchronizedField;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public final class SyncEntityScanner {

    public static List<SyncField> scanFields(Class<?> clazz) {
        List<SyncField> result = new ArrayList<>();
        for (Field field : getAllFields(clazz)) {
            if (field.isAnnotationPresent(SyncIgnore.class)) continue;
            SynchronizedField ann = field.getAnnotation(SynchronizedField.class);
            if (ann != null) {
                result.add(new SyncField(field.getName(), ann.strategy(), ann.delayMs(), ann.scope()));
            }
        }
        return result;
    }

    public static SynchronizedEntity findEntityAnnotation(Class<?> clazz) {
        return clazz.getAnnotation(SynchronizedEntity.class);
    }

    public static SyncScope resolveScope(Class<?> clazz) {
        SynchronizedEntity ann = findEntityAnnotation(clazz);
        return ann != null ? ann.scope() : SyncScope.SERVER;
    }

    public static SyncDirection resolveDirection(Class<?> clazz) {
        SynchronizedEntity ann = findEntityAnnotation(clazz);
        return ann != null ? ann.direction() : SyncDirection.BIDIRECTIONAL;
    }

    private static List<Field> getAllFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        Class<?> current = clazz;
        while (current != null && current != Object.class) {
            for (Field field : current.getDeclaredFields()) {
                fields.add(field);
            }
            current = current.getSuperclass();
        }
        return fields;
    }
}
