package pl.fejzu.persistence.sector.core;

import pl.fejzu.persistence.sector.LastSectorProvider;
import pl.fejzu.persistence.sector.Sector;
import pl.fejzu.persistence.sector.SectorConnectRequest;
import pl.fejzu.persistence.sector.SectorConnectResult;
import pl.fejzu.persistence.sector.SectorId;
import pl.fejzu.persistence.sector.SectorJoinResult;
import pl.fejzu.persistence.sector.SectorJoinStrategy;
import pl.fejzu.persistence.sector.SectorRegistry;
import pl.fejzu.persistence.settings.SectorSettings;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public final class DefaultSectorConnectService {

    private final SectorSelectionService selectionService;
    private final SectorRegistry registry;
    private final LastSectorProvider lastSectorProvider;
    private final SectorSettings settings;

    public DefaultSectorConnectService(
        SectorSelectionService selectionService,
        SectorRegistry registry,
        LastSectorProvider lastSectorProvider,
        SectorSettings settings
    ) {
        this.selectionService = selectionService;
        this.registry = registry;
        this.lastSectorProvider = lastSectorProvider;
        this.settings = settings;
    }

    public CompletableFuture<SectorConnectResult> connectOnJoin(SectorConnectRequest request) {
        return selectionService.selectSector(request).thenApply(joinResult -> {
            if (joinResult.isFailure()) {
                return SectorConnectResult.failure(request.getPlayerId(), joinResult.getErrorMessage());
            }
            SectorId selectedId = joinResult.getSelectedSector();
            Optional<Sector> sector = registry.getSector(selectedId);
            if (sector.isEmpty()) {
                return SectorConnectResult.failure(request.getPlayerId(), "Sector not found: " + selectedId);
            }
            return SectorConnectResult.success(
                request.getPlayerId(),
                selectedId,
                sector.get().getServerName(),
                joinResult.getUsedStrategy()
            );
        });
    }

    public CompletableFuture<Void> saveLastSector(UUID playerId, SectorId sectorId) {
        if (!settings.isSaveLastSectorOnQuit()) return CompletableFuture.completedFuture(null);
        return lastSectorProvider.saveLastSector(playerId, sectorId);
    }

    public CompletableFuture<Void> updateLastSector(UUID playerId, SectorId sectorId) {
        if (!settings.isUpdateLastSectorOnTransfer()) return CompletableFuture.completedFuture(null);
        return lastSectorProvider.saveLastSector(playerId, sectorId);
    }
}
