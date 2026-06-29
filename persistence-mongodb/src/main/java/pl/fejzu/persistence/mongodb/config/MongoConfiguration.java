package pl.fejzu.persistence.mongodb.config;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public final class MongoConfiguration {

    @Builder.Default
    private final String connectionString = "mongodb://localhost:27017";

    private final String database;

    @Builder.Default
    private final List<String> entityPackages = List.of();

    @Builder.Default
    private final boolean autoEnsureIndexes = true;

    @Builder.Default
    private final int connectTimeoutMs = 5000;

    @Builder.Default
    private final int socketTimeoutMs = 30000;

    @Builder.Default
    private final int maxPoolSize = 10;

    public static MongoConfiguration defaults(String database) {
        return MongoConfiguration.builder()
            .database(database)
            .build();
    }

    public static MongoConfiguration of(String connectionString, String database) {
        return MongoConfiguration.builder()
            .connectionString(connectionString)
            .database(database)
            .build();
    }
}
