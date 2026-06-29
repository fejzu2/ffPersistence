package pl.fejzu.persistence.sector;

import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value
@Builder
public class SectorConnectResult {

    UUID playerId;
    boolean success;
    SectorId sectorId;
    String serverName;
    SectorJoinStrategy usedStrategy;
    String errorMessage;

    public boolean isFailure() {
        return !success;
    }

    public static SectorConnectResult success(UUID playerId, SectorId sectorId, String serverName, SectorJoinStrategy strategy) {
        return SectorConnectResult.builder()
            .playerId(playerId)
            .success(true)
            .sectorId(sectorId)
            .serverName(serverName)
            .usedStrategy(strategy)
            .build();
    }

    public static SectorConnectResult failure(UUID playerId, String errorMessage) {
        return SectorConnectResult.builder()
            .playerId(playerId)
            .success(false)
            .errorMessage(errorMessage)
            .build();
    }
}
