package pl.fejzu.persistence.sector;

import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value
@Builder
public class SectorConnectRequest {

    UUID playerId;
    String playerName;
    SectorJoinStrategy strategy;
    SectorId preferredSector;
    boolean preload;
    long timeoutMs;
}
