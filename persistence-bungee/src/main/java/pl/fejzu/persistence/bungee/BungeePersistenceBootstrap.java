package pl.fejzu.persistence.bungee;

import lombok.Getter;
import net.md_5.bungee.api.plugin.Plugin;
import pl.fejzu.persistence.bungee.messaging.BungeePluginMessagingBridge;
import pl.fejzu.persistence.bungee.platform.BungeePlatformAdapter;
import pl.fejzu.persistence.bungee.proxy.BungeeProxyBridge;
import pl.fejzu.persistence.bungee.queue.BungeeQueueService;
import pl.fejzu.persistence.bungee.sector.BungeeSectorIntegration;
import pl.fejzu.persistence.bungee.transfer.BungeePlayerTransferService;
import pl.fejzu.persistence.core.lifecycle.LifecycleService;
import pl.fejzu.persistence.service.PersistenceService;
import pl.fejzu.persistence.settings.ProxySettings;
import pl.fejzu.persistence.settings.SectorSettings;

@Getter
public final class BungeePersistenceBootstrap {

    private final Plugin plugin;
    private final PersistenceService service;

    private final BungeePlatformAdapter platformAdapter;
    private final BungeePluginMessagingBridge messagingBridge;
    private final BungeeProxyBridge proxyBridge;
    private final BungeeQueueService queueService;
    private final BungeePlayerTransferService playerTransferService;

    private final LifecycleService lifecycleService = new LifecycleService();
    private BungeeSectorIntegration sectorIntegration;

    public BungeePersistenceBootstrap(Plugin plugin, PersistenceService service) {
        this.plugin = plugin;
        this.service = service;

        ProxySettings proxySettings = service.getContext().getSettings().getProxy();

        this.platformAdapter = new BungeePlatformAdapter(plugin.getProxy());
        this.messagingBridge = new BungeePluginMessagingBridge(plugin);
        this.proxyBridge = new BungeeProxyBridge(plugin.getProxy(), messagingBridge);
        this.queueService = new BungeeQueueService(messagingBridge, proxySettings);
        this.playerTransferService = new BungeePlayerTransferService(plugin.getProxy(), proxyBridge, queueService);
    }

    public BungeePersistenceBootstrap sectorIntegration(boolean enable) {
        if (!enable) return this;
        service.getContext().getSectorService().ifPresent(sectorService -> {
            SectorSettings settings = service.getContext().getSettings().getSector();
            this.sectorIntegration = new BungeeSectorIntegration(plugin, sectorService, settings);
            if (settings.isProxyAutoConnectEnabled()) {
                sectorIntegration.register();
            }
        });
        return this;
    }

    public BungeePersistenceBootstrap register() {
        lifecycleService.register(service);
        lifecycleService.start();
        messagingBridge.register();
        return this;
    }

    public void unregister() {
        messagingBridge.unregister();
        lifecycleService.shutdown();
    }
}
