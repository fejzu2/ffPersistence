package pl.fejzu.persistence.mongodb;

import com.mongodb.client.MongoClient;
import dev.morphia.Datastore;
import dev.morphia.Morphia;
import pl.fejzu.persistence.mongodb.config.MongoConfiguration;

public final class MorphiaDatastoreFactory {

    private final MongoConfiguration configuration;

    public MorphiaDatastoreFactory(MongoConfiguration configuration) {
        this.configuration = configuration;
    }

    public Datastore createDatastore(MongoClient client) {
        Datastore datastore = Morphia.createDatastore(client, configuration.getDatabase());

        for (String pkg : configuration.getEntityPackages()) {
            datastore.getMapper().mapPackage(pkg);
        }

        if (configuration.isAutoEnsureIndexes()) {
            datastore.ensureIndexes();
        }

        return datastore;
    }
}
