package pl.fejzu.persistence.sector;

import java.util.Map;
import java.util.Optional;

public interface Sector {

    SectorId getId();

    String getDisplayName();

    String getServerName();

    SectorState getState();

    Optional<SectorBounds> getBounds();

    Map<String, String> getMetadata();

    int getMaxPlayers();

    int getPriority();

    boolean isOnline();
}
