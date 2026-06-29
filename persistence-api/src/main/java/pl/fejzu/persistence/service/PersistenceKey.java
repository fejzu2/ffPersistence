package pl.fejzu.persistence.service;

import lombok.Value;

@Value
public class PersistenceKey {

    Class<?> entityType;
    Object id;

    public static <ID> PersistenceKey of(Class<?> entityType, ID id) {
        return new PersistenceKey(entityType, id);
    }
}
