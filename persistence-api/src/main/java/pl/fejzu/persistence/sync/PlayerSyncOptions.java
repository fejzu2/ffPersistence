package pl.fejzu.persistence.sync;

import lombok.Builder;
import lombok.Value;

import java.util.Set;

@Value
@Builder
public class PlayerSyncOptions {

    boolean dirtyOnly;
    Set<String> fields;
    SyncTarget target;
    SyncReason reason;
    SyncStrategy strategy;

    public static PlayerSyncOptions defaults() {
        return PlayerSyncOptions.builder()
            .dirtyOnly(true)
            .target(SyncTarget.broadcast())
            .reason(SyncReason.MANUAL)
            .strategy(SyncStrategy.INSTANT)
            .build();
    }

    public static PlayerSyncOptions fullSync(SyncTarget target) {
        return PlayerSyncOptions.builder()
            .dirtyOnly(false)
            .target(target)
            .reason(SyncReason.MANUAL)
            .strategy(SyncStrategy.INSTANT)
            .build();
    }
}
