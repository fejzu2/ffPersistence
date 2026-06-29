package pl.fejzu.persistence.sync.core;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public final class SyncFieldResolver {

    public Map<String, Object> extractFields(Object entity, Set<String> fieldNames) {
        Map<String, Object> result = new LinkedHashMap<>();
        Class<?> clazz = entity.getClass();
        for (String name : fieldNames) {
            Field field = findField(clazz, name);
            if (field == null) continue;
            try {
                field.setAccessible(true);
                result.put(name, field.get(entity));
            } catch (IllegalAccessException ignored) {
            }
        }
        return result;
    }

    public void applyFields(Object entity, Map<String, Object> fields) {
        Class<?> clazz = entity.getClass();
        for (Map.Entry<String, Object> entry : fields.entrySet()) {
            Field field = findField(clazz, entry.getKey());
            if (field == null) continue;
            try {
                field.setAccessible(true);
                field.set(entity, entry.getValue());
            } catch (IllegalAccessException ignored) {
            }
        }
    }

    private Field findField(Class<?> clazz, String name) {
        Class<?> current = clazz;
        while (current != null && current != Object.class) {
            try {
                return current.getDeclaredField(name);
            } catch (NoSuchFieldException ignored) {
                current = current.getSuperclass();
            }
        }
        return null;
    }
}
