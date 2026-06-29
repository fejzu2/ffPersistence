package pl.fejzu.persistence.sector;

import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value
@Builder
public class SectorTransferRequest {

    UUID playerId;
    String playerName;
    SectorId sourceSector;
    SectorId targetSector;
    SectorTransferReason reason;
    boolean preload;
    boolean lockData;
    boolean syncFullPlayer;
}
