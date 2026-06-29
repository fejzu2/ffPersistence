package pl.fejzu.persistence.settings;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import pl.fejzu.persistence.sector.SectorJoinStrategy;

@Builder(toBuilder = true)
@Getter
@EqualsAndHashCode
public final class SectorSettings {

    @Builder.Default
    private final boolean enabled = false;

    @Builder.Default
    private final String currentSector = "default";

    @Builder.Default
    private final String currentServerName = "";

    @Builder.Default
    private final long transferTimeoutSeconds = 15;

    @Builder.Default
    private final boolean preloadTarget = true;

    @Builder.Default
    private final boolean lockPlayerDataDuringTransfer = true;

    @Builder.Default
    private final boolean fullSyncOnTransfer = true;

    @Builder.Default
    private final boolean unloadSourceCacheAfterTransfer = false;

    @Builder.Default
    private final String fallbackSector = "lobby";

    @Builder.Default
    private final boolean borderDetectionEnabled = false;

    @Builder.Default
    private final long borderCheckIntervalTicks = 20L;

    @Builder.Default
    private final boolean connectToLastSectorOnJoin = false;

    @Builder.Default
    private final boolean saveLastSectorOnQuit = true;

    @Builder.Default
    private final boolean updateLastSectorOnTransfer = true;

    @Builder.Default
    private final SectorJoinStrategy joinStrategy = SectorJoinStrategy.LAST_SECTOR;

    @Builder.Default
    private final String lobbySector = "lobby";

    @Builder.Default
    private final boolean allowFallbackWhenLastOffline = true;

    @Builder.Default
    private final String lastSectorFieldName = "lastSector";

    @Builder.Default
    private final boolean validateLastSectorOnline = true;

    @Builder.Default
    private final boolean proxyAutoConnectEnabled = false;

    public static SectorSettings defaults() {
        return builder().build();
    }

    public static SectorSettings enabled(String sector) {
        return builder().enabled(true).currentSector(sector).build();
    }
}
