package pl.fejzu.persistence.settings;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.Duration;

@Builder(toBuilder = true)
@Getter
@EqualsAndHashCode
public final class CacheSettings {

    @Builder.Default
    private final boolean enabled = true;

    @Builder.Default
    private final Duration expireAfterWrite = Duration.ofMinutes(30);

    @Builder.Default
    private final Duration expireAfterAccess = Duration.ofMinutes(10);

    @Builder.Default
    private final long maxSize = 1000;

    @Builder.Default
    private final boolean saveOnEvict = true;

    @Builder.Default
    private final Duration cleanupInterval = Duration.ofMinutes(5);

    public static CacheSettings defaults() {
        return builder().build();
    }

    public static CacheSettings disabled() {
        return builder().enabled(false).build();
    }

    public static CacheSettings aggressive() {
        return builder()
            .expireAfterWrite(Duration.ofHours(2))
            .expireAfterAccess(Duration.ofHours(1))
            .maxSize(10000)
            .build();
    }
}
