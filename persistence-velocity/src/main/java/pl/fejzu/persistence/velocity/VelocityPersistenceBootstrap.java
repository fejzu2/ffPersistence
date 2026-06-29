package pl.fejzu.persistence.velocity;

import com.velocitypowered.api.proxy.ProxyServer;
import lombok.Getter;
import pl.fejzu.persistence.core.lifecycle.LifecycleService;
import pl.fejzu.persistence.service.PersistenceService;
import pl.fejzu.persistence.settings.ProxySettings;
import pl.fejzu.persistence.settings.SectorSettings;
import pl.fejzu.persistence.velocity.messaging.VelocityPluginMessagingBridge;
import pl.fejzu.persistence.velocity.platform.VelocityPlatformAdapter;
import pl.fejzu.persistence.velocity.proxy.VelocityProxyBridge;
import pl.fejzu.persistence.velocity.queue.VelocityQueueService;
import pl.fejzu.persistence.velocity.sector.VelocitySectorIntegration;
import pl.fejzu.persistence.velocity.transfer.VelocityPlayerTransferService;

@Getter
public final class VelocityPersistenceBootstrap {

    private final Object pluginInstance;
    private final ProxyServer proxy;
    private final PersistenceService service;

    private final VelocityPlatformAdapter platformAdapter;
    private final VelocityPluginMessagingBridge messagingBridge;
    private final VelocityProxyBridge proxyBridge;
    private final VelocityQueueService queueService;
    private final VelocityPlayerTransferService playerTransferService;

    private final LifecycleService lifecycleService = new LifecycleService();
    private VelocitySectorIntegration sectorIntegration;

    public VelocityPersistenceBootstrap(Object pluginInstance, ProxyServer proxy, PersistenceService service) {
        this.pluginInstance = pluginInstance;
        this.proxy = proxy;
        this.service = service;

        ProxySettings proxySettings = service.getContext().getSettings().getProxy();

        this.platformAdapter = new VelocityPlatformAdapter(proxy);
        this.messagingBridge = new VelocityPluginMessagingBridge(proxy);
        this.proxyBridge = new VelocityProxyBridge(proxy, messagingBridge);
        this.queueService = new VelocityQueueService(proxy, messagingBridge, proxySettings);
        this.playerTransferService = new VelocityPlayerTransferService(proxy, proxyBridge, queueService);
    }

    public VelocityPersistenceBootstrap sectorIntegration(boolean enable) {
        if (!enable) return this;
        service.getContext().getSectorService().ifPresent(sectorService -> {
            SectorSettings settings = service.getContext().getSettings().getSector();
            this.sectorIntegration = new VelocitySectorIntegration(proxy, pluginInstance, sectorService, settings);
            if (settings.isProxyAutoConnectEnabled()) {
                sectorIntegration.register();
            }
        });
        return this;
    }

    public VelocityPersistenceBootstrap register() {
        lifecycleService.register(service);
        lifecycleService.start();
        proxy.getEventManager().register(pluginInstance, messagingBridge);
        return this;
    }

    public void unregister() {
        lifecycleService.shutdown();
    }
}
