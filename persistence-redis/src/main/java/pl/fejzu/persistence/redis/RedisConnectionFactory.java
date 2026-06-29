package pl.fejzu.persistence.redis;

import pl.fejzu.persistence.redis.config.RedisConfiguration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public final class RedisConnectionFactory {

    private final RedisConfiguration configuration;

    public RedisConnectionFactory(RedisConfiguration configuration) {
        this.configuration = configuration;
    }

    public JedisPool createPool() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(configuration.getMaxPoolSize());
        poolConfig.setTestOnBorrow(true);
        poolConfig.setTestOnReturn(true);
        poolConfig.setTestWhileIdle(true);

        if (configuration.getPassword() != null && !configuration.getPassword().isBlank()) {
            return new JedisPool(
                poolConfig,
                configuration.getHost(),
                configuration.getPort(),
                configuration.getConnectTimeoutMs(),
                configuration.getPassword(),
                configuration.getDatabase(),
                configuration.isSsl()
            );
        }

        return new JedisPool(
            poolConfig,
            configuration.getHost(),
            configuration.getPort(),
            configuration.getConnectTimeoutMs(),
            null,
            configuration.getDatabase(),
            configuration.isSsl()
        );
    }
}
