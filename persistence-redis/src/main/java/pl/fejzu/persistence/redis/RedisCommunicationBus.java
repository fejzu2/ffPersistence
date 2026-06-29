package pl.fejzu.persistence.redis;

import pl.fejzu.persistence.communication.CommunicationBus;
import pl.fejzu.persistence.communication.CommunicationChannel;
import pl.fejzu.persistence.communication.CommunicationMessage;
import pl.fejzu.persistence.redis.config.RedisConfiguration;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.logging.Logger;

public final class RedisCommunicationBus implements CommunicationBus {

    private static final Logger LOGGER = Logger.getLogger(RedisCommunicationBus.class.getName());

    private final RedisConfiguration configuration;
    private final RedisConnectionFactory connectionFactory;

    private JedisPool pool;
    private RedisPublisher publisher;
    private RedisSubscriber subscriber;
    private ExecutorService subscribeExecutor;
    private volatile boolean connected = false;
    private final Set<String> subscribedChannels = ConcurrentHashMap.newKeySet();

    public RedisCommunicationBus(RedisConfiguration configuration) {
        this.configuration = configuration;
        this.connectionFactory = new RedisConnectionFactory(configuration);
    }

    @Override
    public void connect() {
        if (connected) return;
        LOGGER.info("Connecting to Redis: " + configuration.getHost() + ":" + configuration.getPort());
        pool = connectionFactory.createPool();
        publisher = new RedisPublisher(pool);
        subscriber = new RedisSubscriber();
        subscribeExecutor = Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r, "persistence-redis-subscriber");
            t.setDaemon(true);
            return t;
        });
        connected = true;
        LOGGER.info("Connected to Redis");
    }

    @Override
    public void disconnect() {
        if (!connected) return;
        if (subscriber != null && subscriber.isSubscribed()) {
            subscriber.unsubscribe();
        }
        if (subscribeExecutor != null) {
            subscribeExecutor.shutdownNow();
        }
        if (pool != null) {
            pool.close();
        }
        connected = false;
        LOGGER.info("Disconnected from Redis");
    }

    @Override
    public void publish(CommunicationChannel channel, CommunicationMessage message) {
        if (!connected) throw new IllegalStateException("Redis bus is not connected");
        publisher.publish(channel.getName(), message);
    }

    @Override
    public void subscribe(CommunicationChannel channel, Consumer<CommunicationMessage> handler) {
        if (!connected) throw new IllegalStateException("Redis bus is not connected");
        String channelName = channel.getName();
        subscriber.addHandler(channelName, handler::accept);

        if (!subscribedChannels.contains(channelName)) {
            subscribedChannels.add(channelName);
            subscribeExecutor.submit(() -> {
                try (Jedis jedis = pool.getResource()) {
                    jedis.subscribe(subscriber, subscribedChannels.toArray(new String[0]));
                }
            });
        }
    }

    @Override
    public void unsubscribe(CommunicationChannel channel) {
        subscriber.removeHandler(channel.getName());
        subscribedChannels.remove(channel.getName());
    }

    @Override
    public boolean isConnected() {
        return connected;
    }

    @Override
    public String getBusName() {
        return "Redis";
    }
}
