package pl.fejzu.persistence.bungee.sector;

import lombok.Getter;
import net.md_5.bungee.api.plugin.Plugin;
import pl.fejzu.persistence.sector.SectorService;
import pl.fejzu.persistence.sector.core.DefaultLastSectorResolver;
import pl.fejzu.persistence.sector.core.DefaultSectorConnectService;
import pl.fejzu.persistence.sector.core.SectorAvailabilityService;
import pl.fejzu.persistence.sector.core.SectorSelectionService;
import pl.fejzu.persistence.settings.SectorSettings;

@Getter
public final class BungeeSectorIntegration {

    private final Plugin plugin;
    private final BungeeLastSectorConnector lastSectorConnector;
    private final BungeeSectorConnectService connectService;
    private final BungeeSectorJoinListener joinListener;

    public BungeeSectorIntegration(Plugin plugin, SectorService sectorService, SectorSettings settings) {
        this.plugin = plugin;

        this.lastSectorConnector = new BungeeLastSectorConnector();

        DefaultLastSectorResolver resolver = new DefaultLastSectorResolver(lastSectorConnector, sectorService.registry(), settings);
        SectorAvailabilityService availability = new SectorAvailabilityService(sectorService.registry());
        SectorSelectionService selection = new SectorSelectionService(resolver, availability);
        DefaultSectorConnectService sectorConnectService = new DefaultSectorConnectService(selection, sectorService.registry(), lastSectorConnector, settings);

        this.connectService = new BungeeSectorConnectService(sectorConnectService, settings);
        this.joinListener = new BungeeSectorJoinListener(plugin.getProxy(), connectService);
    }

    public void register() {
        plugin.getProxy().getPluginManager().registerListener(plugin, joinListener);
    }
}
