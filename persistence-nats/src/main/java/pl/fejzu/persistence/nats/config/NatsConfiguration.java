package pl.fejzu.persistence.nats.config;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public final class NatsConfiguration {

    @Builder.Default
    private final String url = "nats://localhost:4222";

    private final String username;
    private final String password;
    private final String token;

    @Builder.Default
    private final int connectionTimeoutMs = 5000;

    @Builder.Default
    private final int maxReconnects = 10;

    @Builder.Default
    private final long reconnectWaitMs = 1000;

    @Builder.Default
    private final String subjectPrefix = "ffpersistence";

    public static NatsConfiguration defaults() {
        return NatsConfiguration.builder().build();
    }

    public static NatsConfiguration of(String url) {
        return NatsConfiguration.builder().url(url).build();
    }
}
