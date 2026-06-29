package pl.fejzu.persistence.redis;

import pl.fejzu.persistence.redis.codec.RedisMessageCodec;
import redis.clients.jedis.JedisPubSub;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public final class RedisSubscriber extends JedisPubSub {

    private final Map<String, Consumer<RedisMessage>> handlers = new ConcurrentHashMap<>();
    private final RedisMessageCodec codec = new RedisMessageCodec();

    @Override
    public void onMessage(String channel, String message) {
        Consumer<RedisMessage> handler = handlers.get(channel);
        if (handler != null) {
            RedisMessage decoded = codec.decode(message);
            handler.accept(decoded);
        }
    }

    public void addHandler(String channel, Consumer<RedisMessage> handler) {
        handlers.put(channel, handler);
    }

    public void removeHandler(String channel) {
        handlers.remove(channel);
    }
}
