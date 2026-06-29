package pl.fejzu.persistence.redis;

import pl.fejzu.persistence.communication.CommunicationMessage;
import pl.fejzu.persistence.redis.codec.RedisMessageCodec;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public final class RedisPublisher {

    private final JedisPool pool;
    private final RedisMessageCodec codec = new RedisMessageCodec();

    public RedisPublisher(JedisPool pool) {
        this.pool = pool;
    }

    public void publish(String channel, CommunicationMessage message) {
        String encoded = codec.encode(message);
        try (Jedis jedis = pool.getResource()) {
            jedis.publish(channel, encoded);
        }
    }

    public void publishRaw(String channel, String message) {
        try (Jedis jedis = pool.getResource()) {
            jedis.publish(channel, message);
        }
    }
}
