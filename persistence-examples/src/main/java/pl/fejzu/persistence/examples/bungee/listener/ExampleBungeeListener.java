package pl.fejzu.persistence.examples.bungee.listener;

import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import pl.fejzu.persistence.examples.common.factory.ExampleLogger;
import pl.fejzu.persistence.examples.common.sector.ExampleLastSectorProvider;
import pl.fejzu.persistence.sector.SectorService;
import pl.fejzu.persistence.service.PersistenceService;

public final class ExampleBungeeListener implements Listener {

    private static final ExampleLogger LOG = ExampleLogger.of(ExampleBungeeListener.class);

    private final PersistenceService service;
    private final SectorService sectorService;
    private final ExampleLastSectorProvider lastSectorProvider;

    public ExampleBungeeListener(
        PersistenceService service,
        SectorService sectorService,
        ExampleLastSectorProvider lastSectorProvider
    ) {
        this.service = service;
        this.sectorService = sectorService;
        this.lastSectorProvider = lastSectorProvider;
    }

    @EventHandler
    public void onPostLogin(PostLoginEvent event) {
        LOG.info("Player " + event.getPlayer().getName() + " joined - sector routing handled by BungeeSectorIntegration");
    }

    @EventHandler
    public void onDisconnect(PlayerDisconnectEvent event) {
        sectorService.playerTracker().getCurrentSector(event.getPlayer().getUniqueId()).ifPresent(currentSector -> {
            lastSectorProvider.saveLastSector(event.getPlayer().getUniqueId(), currentSector)
                .thenRun(() -> LOG.info("Saved last sector for " + event.getPlayer().getName() + ": " + currentSector));
        });
    }
}
