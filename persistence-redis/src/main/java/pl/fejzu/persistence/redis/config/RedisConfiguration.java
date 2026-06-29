package pl.fejzu.persistence.redis.config;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public final class RedisConfiguration {

    @Builder.Default
    private final String host = "localhost";

    @Builder.Default
    private final int port = 6379;

    private final String password;

    @Builder.Default
    private final int database = 0;

    @Builder.Default
    private final int connectTimeoutMs = 5000;

    @Builder.Default
    private final int socketTimeoutMs = 10000;

    @Builder.Default
    private final int maxPoolSize = 10;

    @Builder.Default
    private final boolean ssl = false;

    public static RedisConfiguration defaults() {
        return RedisConfiguration.builder().build();
    }

    public static RedisConfiguration of(String host, int port) {
        return RedisConfiguration.builder()
            .host(host)
            .port(port)
            .build();
    }

    public static RedisConfiguration of(String host, int port, String password) {
        return RedisConfiguration.builder()
            .host(host)
            .port(port)
            .password(password)
            .build();
    }
}
