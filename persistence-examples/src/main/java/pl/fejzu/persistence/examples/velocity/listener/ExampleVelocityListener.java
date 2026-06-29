package pl.fejzu.persistence.examples.velocity.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.player.PlayerChooseInitialServerEvent;
import pl.fejzu.persistence.examples.common.factory.ExampleLogger;
import pl.fejzu.persistence.examples.common.sector.ExampleLastSectorProvider;
import pl.fejzu.persistence.sector.SectorId;
import pl.fejzu.persistence.sector.SectorService;
import pl.fejzu.persistence.service.PersistenceService;

public final class ExampleVelocityListener {

    private static final ExampleLogger LOG = ExampleLogger.of(ExampleVelocityListener.class);

    private final PersistenceService service;
    private final SectorService sectorService;
    private final ExampleLastSectorProvider lastSectorProvider;

    public ExampleVelocityListener(
        PersistenceService service,
        SectorService sectorService,
        ExampleLastSectorProvider lastSectorProvider
    ) {
        this.service = service;
        this.sectorService = sectorService;
        this.lastSectorProvider = lastSectorProvider;
    }

    @Subscribe
    public void onChooseInitialServer(PlayerChooseInitialServerEvent event) {
        LOG.info("Player " + event.getPlayer().getUsername() + " joining - sector resolution handled by VelocitySectorIntegration");
    }

    @Subscribe
    public void onDisconnect(DisconnectEvent event) {
        sectorService.playerTracker().getCurrentSector(event.getPlayer().getUniqueId()).ifPresent(currentSector -> {
            lastSectorProvider.saveLastSector(event.getPlayer().getUniqueId(), currentSector)
                .thenRun(() -> LOG.info("Saved last sector for " + event.getPlayer().getUsername() + ": " + currentSector));
        });
    }
}
