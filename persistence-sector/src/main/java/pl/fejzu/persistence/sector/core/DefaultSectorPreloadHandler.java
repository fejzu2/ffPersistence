package pl.fejzu.persistence.sector.core;

import pl.fejzu.persistence.sector.SectorId;
import pl.fejzu.persistence.sector.SectorPreloadHandler;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public final class DefaultSectorPreloadHandler implements SectorPreloadHandler {

    private final Map<UUID, CompletableFuture<Void>> pendingPreloads = new ConcurrentHashMap<>();
    private final Map<UUID, CompletableFuture<Boolean>> readyFutures = new ConcurrentHashMap<>();

    @Override
    public CompletableFuture<Void> preload(UUID playerId, SectorId targetSector) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        pendingPreloads.put(playerId, future);
        return future;
    }

    @Override
    public CompletableFuture<Boolean> awaitReady(UUID playerId, long timeoutMs) {
        CompletableFuture<Boolean> future = readyFutures.computeIfAbsent(playerId, id -> new CompletableFuture<>());
        return future.orTimeout(timeoutMs, TimeUnit.MILLISECONDS)
            .exceptionally(e -> false);
    }

    @Override
    public void notifyReady(UUID playerId) {
        CompletableFuture<Boolean> future = readyFutures.remove(playerId);
        if (future != null) {
            future.complete(true);
        }
        CompletableFuture<Void> preload = pendingPreloads.remove(playerId);
        if (preload != null) {
            preload.complete(null);
        }
    }
}
