package pl.fejzu.persistence.settings;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import pl.fejzu.persistence.sync.SyncConflictStrategy;
import pl.fejzu.persistence.sync.SyncScope;

import java.time.Duration;

@Builder(toBuilder = true)
@Getter
@EqualsAndHashCode
public final class SyncSettings {

    @Builder.Default
    private final boolean enabled = false;

    @Builder.Default
    private final boolean autoPlayerSync = true;

    @Builder.Default
    private final boolean syncOnJoin = true;

    @Builder.Default
    private final boolean syncOnQuit = true;

    @Builder.Default
    private final boolean syncOnTransfer = true;

    @Builder.Default
    private final boolean syncDirtyOnly = true;

    @Builder.Default
    private final SyncStrategy defaultStrategy = SyncStrategy.INSTANT;

    @Builder.Default
    private final SyncScope defaultScope = SyncScope.SERVER;

    @Builder.Default
    private final SyncConflictStrategy conflictStrategy = SyncConflictStrategy.LAST_WRITE_WINS;

    @Builder.Default
    private final long debounceDelayMs = 500;

    @Builder.Default
    private final Duration batchInterval = Duration.ofSeconds(5);

    @Builder.Default
    private final boolean includeTransientFields = false;

    @Builder.Default
    private final int maxPacketSize = 65536;

    @Builder.Default
    private final boolean compressionEnabled = false;

    public static SyncSettings defaults() {
        return builder().build();
    }

    public static SyncSettings enabled() {
        return builder().enabled(true).build();
    }
}
