package pl.fejzu.persistence.examples.common.factory;

import pl.fejzu.persistence.core.service.DefaultPersistenceService;
import pl.fejzu.persistence.mongodb.MongoStorageProvider;
import pl.fejzu.persistence.mongodb.config.MongoConfiguration;
import pl.fejzu.persistence.nats.NatsCommunicationBus;
import pl.fejzu.persistence.nats.config.NatsConfiguration;
import pl.fejzu.persistence.redis.RedisCommunicationBus;
import pl.fejzu.persistence.redis.config.RedisConfiguration;
import pl.fejzu.persistence.settings.PersistenceSettings;

import java.util.List;

public final class ExamplePersistenceFactory {

    private static final ExampleLogger LOG = ExampleLogger.of(ExamplePersistenceFactory.class);

    private ExamplePersistenceFactory() {}

    public static DefaultPersistenceService createPaperService() {
        MongoStorageProvider storage = buildMongoStorage();
        storage.connect().join();
        return DefaultPersistenceService.builder()
            .storage(storage)
            .settings(ExampleSettingsFactory.paper())
            .build();
    }

    public static DefaultPersistenceService createVelocityService() {
        MongoStorageProvider storage = buildMongoStorage();
        storage.connect().join();
        RedisCommunicationBus bus = buildRedisBus();
        bus.connect();
        PersistenceSettings settings = ExampleSettingsFactory.withRedis(ExampleSettingsFactory.velocity());
        return DefaultPersistenceService.builder()
            .storage(storage)
            .communication(bus)
            .settings(settings)
            .build();
    }

    public static DefaultPersistenceService createBungeeService() {
        MongoStorageProvider storage = buildMongoStorage();
        storage.connect().join();
        RedisCommunicationBus bus = buildRedisBus();
        bus.connect();
        PersistenceSettings settings = ExampleSettingsFactory.withRedis(ExampleSettingsFactory.bungee());
        return DefaultPersistenceService.builder()
            .storage(storage)
            .communication(bus)
            .settings(settings)
            .build();
    }

    public static RedisCommunicationBus buildRedisBus() {
        String host = env("REDIS_HOST", "localhost");
        int port = Integer.parseInt(env("REDIS_PORT", "6379"));
        String password = System.getProperty("redis.password", System.getenv("REDIS_PASSWORD"));
        return new RedisCommunicationBus(RedisConfiguration.of(host, port, password));
    }

    public static NatsCommunicationBus buildNatsBus() {
        NatsCommunicationBus bus = new NatsCommunicationBus(
            NatsConfiguration.of(env("NATS_URL", "nats://localhost:4222"))
        );
        bus.connect();
        return bus;
    }

    private static MongoStorageProvider buildMongoStorage() {
        String uri = env("MONGO_URI", "mongodb://localhost:27017");
        String database = env("MONGO_DATABASE", "ffpersistence_example");
        LOG.info("Connecting to MongoDB: " + uri + "/" + database);
        return new MongoStorageProvider(
            MongoConfiguration.builder()
                .connectionString(uri)
                .database(database)
                .entityPackages(List.of("pl.fejzu.persistence.examples.common.model"))
                .build()
        );
    }

    private static String env(String key, String defaultValue) {
        String sysProp = System.getProperty(key.toLowerCase().replace('_', '.'));
        if (sysProp != null) return sysProp;
        String envVar = System.getenv(key);
        return envVar != null ? envVar : defaultValue;
    }
}
