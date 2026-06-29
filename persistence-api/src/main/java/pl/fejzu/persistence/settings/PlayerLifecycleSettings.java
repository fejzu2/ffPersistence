package pl.fejzu.persistence.settings;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.Duration;

@Builder(toBuilder = true)
@Getter
@EqualsAndHashCode
public final class PlayerLifecycleSettings {

    @Builder.Default
    private final boolean loadOnJoin = true;

    @Builder.Default
    private final boolean saveOnQuit = true;

    @Builder.Default
    private final boolean unloadOnQuit = true;

    @Builder.Default
    private final boolean saveOnShutdown = true;

    @Builder.Default
    private final Duration saveInterval = Duration.ofMinutes(5);

    @Builder.Default
    private final long loadTimeoutSeconds = 10;

    @Builder.Default
    private final boolean kickOnLoadFailure = true;

    @Builder.Default
    private final String kickMessage = "Failed to load your player data. Please try reconnecting.";

    public static PlayerLifecycleSettings defaults() {
        return builder().build();
    }

    public static PlayerLifecycleSettings noAutoSave() {
        return builder()
            .saveOnQuit(false)
            .saveOnShutdown(false)
            .build();
    }
}
