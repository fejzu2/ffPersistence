package pl.fejzu.persistence.mongodb;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import pl.fejzu.persistence.mongodb.config.MongoConfiguration;

import java.util.concurrent.TimeUnit;

public final class MongoConnectionFactory {

    private final MongoConfiguration configuration;

    public MongoConnectionFactory(MongoConfiguration configuration) {
        this.configuration = configuration;
    }

    public MongoClient createClient() {
        MongoClientSettings settings = MongoClientSettings.builder()
            .applyConnectionString(new ConnectionString(configuration.getConnectionString()))
            .applyToSocketSettings(builder ->
                builder.connectTimeout(configuration.getConnectTimeoutMs(), TimeUnit.MILLISECONDS)
                    .readTimeout(configuration.getSocketTimeoutMs(), TimeUnit.MILLISECONDS)
            )
            .applyToConnectionPoolSettings(builder ->
                builder.maxSize(configuration.getMaxPoolSize())
            )
            .build();

        return MongoClients.create(settings);
    }
}
