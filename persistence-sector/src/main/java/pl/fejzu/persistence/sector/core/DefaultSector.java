package pl.fejzu.persistence.sector.core;

import lombok.Builder;
import lombok.Getter;
import pl.fejzu.persistence.sector.Sector;
import pl.fejzu.persistence.sector.SectorBounds;
import pl.fejzu.persistence.sector.SectorId;
import pl.fejzu.persistence.sector.SectorState;

import java.util.Map;
import java.util.Optional;

@Getter
@Builder(toBuilder = true)
public final class DefaultSector implements Sector {

    private final SectorId id;
    @Builder.Default
    private final String displayName = "";
    private final String serverName;
    @Builder.Default
    private final SectorState state = SectorState.OFFLINE;
    private final SectorBounds bounds;
    @Builder.Default
    private final Map<String, String> metadata = Map.of();
    @Builder.Default
    private final int maxPlayers = -1;
    @Builder.Default
    private final int priority = 0;

    @Override
    public Optional<SectorBounds> getBounds() {
        return Optional.ofNullable(bounds);
    }

    @Override
    public boolean isOnline() {
        return state == SectorState.ONLINE;
    }

    public DefaultSector withState(SectorState newState) {
        return toBuilder().state(newState).build();
    }
}
