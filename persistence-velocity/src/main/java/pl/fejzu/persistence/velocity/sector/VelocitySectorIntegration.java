package pl.fejzu.persistence.velocity.sector;

import com.velocitypowered.api.proxy.ProxyServer;
import lombok.Getter;
import pl.fejzu.persistence.sector.SectorService;
import pl.fejzu.persistence.sector.core.DefaultLastSectorResolver;
import pl.fejzu.persistence.sector.core.DefaultSectorConnectService;
import pl.fejzu.persistence.sector.core.SectorAvailabilityService;
import pl.fejzu.persistence.sector.core.SectorSelectionService;
import pl.fejzu.persistence.settings.SectorSettings;

@Getter
public final class VelocitySectorIntegration {

    private final ProxyServer proxy;
    private final Object pluginInstance;
    private final VelocityLastSectorConnector lastSectorConnector;
    private final VelocitySectorConnectService connectService;
    private final VelocitySectorJoinListener joinListener;

    public VelocitySectorIntegration(ProxyServer proxy, Object pluginInstance, SectorService sectorService, SectorSettings settings) {
        this.proxy = proxy;
        this.pluginInstance = pluginInstance;

        this.lastSectorConnector = new VelocityLastSectorConnector();

        DefaultLastSectorResolver resolver = new DefaultLastSectorResolver(lastSectorConnector, sectorService.registry(), settings);
        SectorAvailabilityService availability = new SectorAvailabilityService(sectorService.registry());
        SectorSelectionService selection = new SectorSelectionService(resolver, availability);
        DefaultSectorConnectService sectorConnectService = new DefaultSectorConnectService(selection, sectorService.registry(), lastSectorConnector, settings);

        this.connectService = new VelocitySectorConnectService(sectorConnectService, settings);
        this.joinListener = new VelocitySectorJoinListener(proxy, connectService);
    }

    public void register() {
        proxy.getEventManager().register(pluginInstance, joinListener);
    }
}
