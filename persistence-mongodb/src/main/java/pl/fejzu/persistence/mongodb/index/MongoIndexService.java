package pl.fejzu.persistence.mongodb.index;

import dev.morphia.Datastore;

import java.util.logging.Logger;

public final class MongoIndexService {

    private static final Logger LOGGER = Logger.getLogger(MongoIndexService.class.getName());

    private final Datastore datastore;

    public MongoIndexService(Datastore datastore) {
        this.datastore = datastore;
    }

    @SuppressWarnings("deprecation")
    public void ensureIndexes() {
        LOGGER.info("Ensuring MongoDB indexes for all mapped entities...");
        datastore.ensureIndexes();
        LOGGER.info("MongoDB indexes ensured");
    }

    @SuppressWarnings("deprecation")
    public void ensureIndexes(Class<?> entityClass) {
        LOGGER.info("Ensuring indexes — scanning all mapped entities (class hint: " + entityClass.getSimpleName() + ")");
        datastore.ensureIndexes();
    }
}
