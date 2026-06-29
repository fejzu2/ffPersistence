package pl.fejzu.persistence.sector;

import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value
@Builder
public class SectorJoinResult {

    UUID playerId;
    boolean success;
    SectorId selectedSector;
    SectorJoinStrategy usedStrategy;
    String errorMessage;
    SectorTransferFailReason failReason;

    public boolean isFailure() {
        return !success;
    }

    public static SectorJoinResult success(UUID playerId, SectorId sector, SectorJoinStrategy strategy) {
        return SectorJoinResult.builder()
            .playerId(playerId)
            .success(true)
            .selectedSector(sector)
            .usedStrategy(strategy)
            .build();
    }

    public static SectorJoinResult failure(UUID playerId, String errorMessage, SectorTransferFailReason reason) {
        return SectorJoinResult.builder()
            .playerId(playerId)
            .success(false)
            .errorMessage(errorMessage)
            .failReason(reason)
            .build();
    }
}
