package pl.fejzu.persistence.sector.core;

import pl.fejzu.persistence.sector.Sector;
import pl.fejzu.persistence.sector.SectorId;
import pl.fejzu.persistence.sector.SectorLockService;
import pl.fejzu.persistence.sector.SectorPlayerTracker;
import pl.fejzu.persistence.sector.SectorPreloadHandler;
import pl.fejzu.persistence.sector.SectorRegistry;
import pl.fejzu.persistence.sector.SectorSyncBridge;
import pl.fejzu.persistence.sector.SectorTransferFailReason;
import pl.fejzu.persistence.sector.SectorTransferHandler;
import pl.fejzu.persistence.sector.SectorTransferRequest;
import pl.fejzu.persistence.sector.SectorTransferResult;
import pl.fejzu.persistence.settings.SectorSettings;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public final class DefaultSectorTransferHandler implements SectorTransferHandler {

    private final SectorRegistry registry;
    private final SectorPlayerTracker playerTracker;
    private final SectorLockService lockService;
    private final SectorPreloadHandler preloadHandler;
    private final SectorSyncBridge syncBridge;
    private final SectorPacketDispatcher packetDispatcher;
    private final SectorSettings settings;
    private final Executor executor;

    public DefaultSectorTransferHandler(
        SectorRegistry registry,
        SectorPlayerTracker playerTracker,
        SectorLockService lockService,
        SectorPreloadHandler preloadHandler,
        SectorSyncBridge syncBridge,
        SectorPacketDispatcher packetDispatcher,
        SectorSettings settings,
        Executor executor
    ) {
        this.registry = registry;
        this.playerTracker = playerTracker;
        this.lockService = lockService;
        this.preloadHandler = preloadHandler;
        this.syncBridge = syncBridge;
        this.packetDispatcher = packetDispatcher;
        this.settings = settings;
        this.executor = executor;
    }

    @Override
    public CompletableFuture<SectorTransferResult> handle(SectorTransferRequest request) {
        return CompletableFuture.supplyAsync(() -> execute(request), executor);
    }

    private SectorTransferResult execute(SectorTransferRequest request) {
        Instant start = Instant.now();
        UUID playerId = request.getPlayerId();
        SectorId targetSectorId = request.getTargetSector();
        long timeoutMs = settings.getTransferTimeoutSeconds() * 1000L;

        Optional<Sector> targetOpt = registry.getSector(targetSectorId);
        if (targetOpt.isEmpty() || !targetOpt.get().isOnline()) {
            return SectorTransferResult.failure(playerId, targetSectorId, SectorTransferFailReason.SECTOR_OFFLINE, "Target sector is offline or not found");
        }

        SectorId sourceSectorId = playerTracker.getCurrentSector(playerId).orElse(null);

        if (request.isLockData()) {
            boolean locked = lockService.lock(playerId, sourceSectorId, targetSectorId, timeoutMs);
            if (!locked) {
                return SectorTransferResult.failure(playerId, targetSectorId, SectorTransferFailReason.LOCK_FAILED, "Player data is already locked");
            }
        }

        try {
            if (request.isSyncFullPlayer() && syncBridge != null) {
                syncBridge.syncPlayerToSector(playerId, targetSectorId).join();
            }

            if (packetDispatcher != null) {
                packetDispatcher.sendTransferStart(request);
            }

            if (request.isPreload()) {
                boolean ready = preloadHandler.awaitReady(playerId, timeoutMs).join();
                if (!ready) {
                    return SectorTransferResult.failure(playerId, targetSectorId, SectorTransferFailReason.TIMEOUT, "Transfer timed out waiting for preload");
                }
            }

            if (sourceSectorId != null) {
                playerTracker.updateLastSector(playerId, sourceSectorId);
            }
            playerTracker.track(playerId, targetSectorId, request.getPlayerName() != null ? request.getPlayerName() : "");

            return SectorTransferResult.success(playerId, targetSectorId, Duration.between(start, Instant.now()));

        } catch (Exception e) {
            return SectorTransferResult.failure(playerId, targetSectorId, SectorTransferFailReason.TRANSFER_FAILED, e.getMessage());
        } finally {
            if (request.isLockData()) {
                lockService.unlock(playerId);
            }
        }
    }
}
