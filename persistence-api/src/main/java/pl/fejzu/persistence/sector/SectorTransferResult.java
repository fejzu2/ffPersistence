package pl.fejzu.persistence.sector;

import lombok.Builder;
import lombok.Value;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Value
@Builder
public class SectorTransferResult {

    UUID playerId;
    SectorId targetSector;
    boolean success;
    SectorTransferState state;
    SectorTransferFailReason failReason;
    String errorMessage;
    Throwable cause;
    Duration duration;

    public boolean isFailure() {
        return !success;
    }

    public static SectorTransferResult success(UUID playerId, SectorId target, Duration duration) {
        return SectorTransferResult.builder()
            .playerId(playerId)
            .targetSector(target)
            .success(true)
            .state(SectorTransferState.COMPLETED)
            .duration(duration)
            .build();
    }

    public static SectorTransferResult failure(UUID playerId, SectorId target, SectorTransferFailReason reason, String message) {
        return SectorTransferResult.builder()
            .playerId(playerId)
            .targetSector(target)
            .success(false)
            .state(SectorTransferState.FAILED)
            .failReason(reason)
            .errorMessage(message)
            .build();
    }
}
