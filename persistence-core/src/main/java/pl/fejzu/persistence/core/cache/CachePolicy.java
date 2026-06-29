package pl.fejzu.persistence.core.cache;

import lombok.Builder;
import lombok.Getter;

import java.time.Duration;

@Getter
@Builder
public final class CachePolicy {

    @Builder.Default
    private final Duration expireAfterWrite = Duration.ofMinutes(30);

    @Builder.Default
    private final Duration expireAfterAccess = Duration.ofMinutes(10);

    @Builder.Default
    private final long maxSize = 1000;

    @Builder.Default
    private final boolean softValues = false;

    public static CachePolicy defaultPolicy() {
        return CachePolicy.builder().build();
    }

    public static CachePolicy noExpiry() {
        return CachePolicy.builder()
            .expireAfterWrite(Duration.ofDays(365))
            .expireAfterAccess(Duration.ofDays(365))
            .build();
    }
}
