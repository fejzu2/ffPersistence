package pl.fejzu.persistence.sector.core;

import lombok.Getter;
import pl.fejzu.persistence.sector.SectorId;
import pl.fejzu.persistence.sector.SectorTransferReason;
import pl.fejzu.persistence.sector.SectorTransferResult;
import pl.fejzu.persistence.sector.SectorTransferSession;
import pl.fejzu.persistence.sector.SectorTransferState;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

@Getter
public final class DefaultSectorTransferSession implements SectorTransferSession {

    private final UUID sessionId = UUID.randomUUID();
    private final UUID playerId;
    private final SectorId sourceSector;
    private final SectorId targetSector;
    private final SectorTransferReason reason;
    private final Instant startedAt = Instant.now();
    private final AtomicReference<SectorTransferState> state = new AtomicReference<>(SectorTransferState.IDLE);
    private volatile Instant completedAt;
    private final CompletableFuture<SectorTransferResult> future = new CompletableFuture<>();

    public DefaultSectorTransferSession(UUID playerId, SectorId sourceSector, SectorId targetSector, SectorTransferReason reason) {
        this.playerId = playerId;
        this.sourceSector = sourceSector;
        this.targetSector = targetSector;
        this.reason = reason;
    }

    @Override
    public SectorTransferState getState() {
        return state.get();
    }

    @Override
    public Optional<Instant> getCompletedAt() {
        return Optional.ofNullable(completedAt);
    }

    @Override
    public CompletableFuture<SectorTransferResult> await() {
        return future;
    }

    public void updateState(SectorTransferState newState) {
        state.set(newState);
    }

    public void complete(SectorTransferResult result) {
        state.set(result.isSuccess() ? SectorTransferState.COMPLETED : SectorTransferState.FAILED);
        completedAt = Instant.now();
        future.complete(result);
    }
}
