package pl.fejzu.persistence.sector;

import lombok.Value;

import java.time.Instant;
import java.util.UUID;

@Value
public class SectorPlayer {

    UUID playerId;
    String playerName;
    SectorId currentSector;
    SectorId lastSector;
    Instant lastSectorChange;
    Instant lastSeen;
}
