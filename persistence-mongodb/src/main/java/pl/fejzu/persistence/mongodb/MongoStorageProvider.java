package pl.fejzu.persistence.mongodb;

import com.mongodb.client.MongoClient;
import dev.morphia.Datastore;
import lombok.Getter;
import pl.fejzu.persistence.mongodb.config.MongoConfiguration;
import pl.fejzu.persistence.storage.StorageProvider;

import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

public final class MongoStorageProvider implements StorageProvider {

    private static final Logger LOGGER = Logger.getLogger(MongoStorageProvider.class.getName());

    private final MongoConfiguration configuration;
    private final MongoConnectionFactory connectionFactory;
    private final MorphiaDatastoreFactory datastoreFactory;

    private MongoClient client;
    @Getter
    private Datastore datastore;
    private volatile boolean connected = false;

    public MongoStorageProvider(MongoConfiguration configuration) {
        this.configuration = configuration;
        this.connectionFactory = new MongoConnectionFactory(configuration);
        this.datastoreFactory = new MorphiaDatastoreFactory(configuration);
    }

    @Override
    public CompletableFuture<Void> connect() {
        return CompletableFuture.runAsync(() -> {
            if (connected) return;
            LOGGER.info("Connecting to MongoDB: " + configuration.getConnectionString());
            client = connectionFactory.createClient();
            datastore = datastoreFactory.createDatastore(client);
            connected = true;
            LOGGER.info("Connected to MongoDB database: " + configuration.getDatabase());
        });
    }

    @Override
    public CompletableFuture<Void> disconnect() {
        return CompletableFuture.runAsync(() -> {
            if (!connected) return;
            if (client != null) {
                client.close();
                client = null;
                datastore = null;
            }
            connected = false;
            LOGGER.info("Disconnected from MongoDB");
        });
    }

    @Override
    public boolean isConnected() {
        return connected;
    }

    @Override
    public String getProviderName() {
        return "MongoDB";
    }
}
