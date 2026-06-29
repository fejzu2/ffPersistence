package pl.fejzu.persistence.sector;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface SectorTransferSession {

    UUID getSessionId();

    UUID getPlayerId();

    SectorId getSourceSector();

    SectorId getTargetSector();

    SectorTransferState getState();

    SectorTransferReason getReason();

    Instant getStartedAt();

    Optional<Instant> getCompletedAt();

    CompletableFuture<SectorTransferResult> await();
}
