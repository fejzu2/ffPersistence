package pl.fejzu.persistence.settings;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Builder(toBuilder = true)
@Getter
@EqualsAndHashCode
public final class StorageSettings {

    @Builder.Default
    private final int connectTimeoutMs = 5000;

    @Builder.Default
    private final int socketTimeoutMs = 30000;

    @Builder.Default
    private final int maxPoolSize = 20;

    @Builder.Default
    private final boolean autoRetry = true;

    @Builder.Default
    private final int maxRetries = 3;

    @Builder.Default
    private final long retryDelayMs = 1000;

    public static StorageSettings defaults() {
        return builder().build();
    }

    public static StorageSettings lenient() {
        return builder()
            .connectTimeoutMs(15000)
            .socketTimeoutMs(60000)
            .maxRetries(5)
            .retryDelayMs(2000)
            .build();
    }
}
