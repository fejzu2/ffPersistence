package pl.fejzu.persistence.mongodb;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import pl.fejzu.persistence.mongodb.config.MongoConfiguration;

public final class MongoDatabaseProvider {

    private final MongoConfiguration configuration;
    @Getter
    private final MongoClient client;

    public MongoDatabaseProvider(MongoConfiguration configuration, MongoClient client) {
        this.configuration = configuration;
        this.client = client;
    }

    public MongoDatabase getDatabase() {
        return client.getDatabase(configuration.getDatabase());
    }

    public MongoDatabase getDatabase(String name) {
        return client.getDatabase(name);
    }

    public void close() {
        client.close();
    }
}
