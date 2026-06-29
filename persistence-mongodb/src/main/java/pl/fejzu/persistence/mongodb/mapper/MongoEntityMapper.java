package pl.fejzu.persistence.mongodb.mapper;

import dev.morphia.Datastore;
import dev.morphia.mapping.Mapper;

import java.util.logging.Logger;

public final class MongoEntityMapper {

    private static final Logger LOGGER = Logger.getLogger(MongoEntityMapper.class.getName());

    private final Mapper mapper;

    public MongoEntityMapper(Datastore datastore) {
        this.mapper = datastore.getMapper();
    }

    public void mapPackage(String packageName) {
        LOGGER.info("Mapping Morphia entities from package: " + packageName);
        mapper.mapPackage(packageName);
    }

    public void mapClass(Class<?> entityClass) {
        LOGGER.info("Mapping Morphia entity: " + entityClass.getName());
        mapper.map(entityClass);
    }

    public boolean isMapped(Class<?> entityClass) {
        return mapper.isMapped(entityClass);
    }
}
