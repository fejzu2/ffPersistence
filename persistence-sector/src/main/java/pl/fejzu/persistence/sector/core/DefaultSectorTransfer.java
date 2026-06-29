package pl.fejzu.persistence.sector.core;

import pl.fejzu.persistence.sector.SectorId;
import pl.fejzu.persistence.sector.SectorTransfer;
import pl.fejzu.persistence.sector.SectorTransferReason;
import pl.fejzu.persistence.sector.SectorTransferRequest;
import pl.fejzu.persistence.sector.SectorTransferResult;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public final class DefaultSectorTransfer implements SectorTransfer {

    private final UUID playerId;
    private final SectorTransferCoordinator coordinator;
    private final DefaultSectorPlayerTracker playerTracker;

    private SectorId targetSector;
    private SectorTransferReason reason = SectorTransferReason.API;
    private boolean preload = true;
    private boolean lockData = true;
    private boolean syncFullPlayer = true;
    private String playerName = "";

    public DefaultSectorTransfer(UUID playerId, SectorTransferCoordinator coordinator, DefaultSectorPlayerTracker playerTracker) {
        this.playerId = playerId;
        this.coordinator = coordinator;
        this.playerTracker = playerTracker;
    }

    @Override
    public SectorTransfer to(SectorId targetSector) {
        this.targetSector = targetSector;
        return this;
    }

    @Override
    public SectorTransfer reason(SectorTransferReason reason) {
        this.reason = reason;
        return this;
    }

    @Override
    public SectorTransfer preload(boolean preload) {
        this.preload = preload;
        return this;
    }

    @Override
    public SectorTransfer lockData(boolean lockData) {
        this.lockData = lockData;
        return this;
    }

    @Override
    public SectorTransfer syncFullPlayer(boolean syncFullPlayer) {
        this.syncFullPlayer = syncFullPlayer;
        return this;
    }

    @Override
    public SectorTransfer playerName(String playerName) {
        this.playerName = playerName;
        return this;
    }

    @Override
    public CompletableFuture<SectorTransferResult> execute() {
        if (targetSector == null) {
            return CompletableFuture.failedFuture(new IllegalStateException("Target sector must be specified"));
        }
        SectorId sourceSector = playerTracker.getCurrentSector(playerId).orElse(null);
        SectorTransferRequest request = SectorTransferRequest.builder()
            .playerId(playerId)
            .playerName(playerName)
            .sourceSector(sourceSector)
            .targetSector(targetSector)
            .reason(reason)
            .preload(preload)
            .lockData(lockData)
            .syncFullPlayer(syncFullPlayer)
            .build();
        return coordinator.execute(request);
    }
}
