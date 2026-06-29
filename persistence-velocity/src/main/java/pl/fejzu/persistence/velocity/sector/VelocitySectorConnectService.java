package pl.fejzu.persistence.velocity.sector;

import pl.fejzu.persistence.sector.SectorConnectRequest;
import pl.fejzu.persistence.sector.SectorConnectResult;
import pl.fejzu.persistence.sector.SectorId;
import pl.fejzu.persistence.sector.core.DefaultSectorConnectService;
import pl.fejzu.persistence.settings.SectorSettings;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public final class VelocitySectorConnectService {

    private final DefaultSectorConnectService connectService;
    private final SectorSettings settings;

    public VelocitySectorConnectService(DefaultSectorConnectService connectService, SectorSettings settings) {
        this.connectService = connectService;
        this.settings = settings;
    }

    public CompletableFuture<SectorConnectResult> connectOnJoin(UUID playerId, String playerName) {
        SectorConnectRequest request = SectorConnectRequest.builder()
            .playerId(playerId)
            .playerName(playerName)
            .strategy(settings.getJoinStrategy())
            .preload(settings.isPreloadTarget())
            .timeoutMs(settings.getTransferTimeoutSeconds() * 1000L)
            .build();
        return connectService.connectOnJoin(request);
    }

    public CompletableFuture<Void> saveLastSector(UUID playerId, SectorId sectorId) {
        return connectService.saveLastSector(playerId, sectorId);
    }
}
