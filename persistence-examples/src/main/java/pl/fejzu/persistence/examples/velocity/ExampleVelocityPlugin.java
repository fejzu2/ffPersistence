package pl.fejzu.persistence.examples.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import pl.fejzu.persistence.core.service.DefaultPersistenceService;
import pl.fejzu.persistence.examples.common.factory.ExampleLogger;
import pl.fejzu.persistence.examples.common.factory.ExamplePersistenceFactory;
import pl.fejzu.persistence.examples.common.factory.ExampleSettingsFactory;
import pl.fejzu.persistence.examples.common.repository.ExampleUserRepository;
import pl.fejzu.persistence.examples.common.sector.ExampleLastSectorProvider;
import pl.fejzu.persistence.examples.common.sector.ExampleSectorFactory;
import pl.fejzu.persistence.examples.common.sync.ExampleSyncFactory;
import pl.fejzu.persistence.examples.velocity.command.ExampleVelocityCommands;
import pl.fejzu.persistence.examples.velocity.listener.ExampleVelocityListener;
import pl.fejzu.persistence.sector.SectorService;
import pl.fejzu.persistence.sector.core.DefaultSectorService;
import pl.fejzu.persistence.settings.PersistenceSettings;
import pl.fejzu.persistence.sync.SyncService;
import pl.fejzu.persistence.sync.core.DefaultSyncService;
import pl.fejzu.persistence.velocity.VelocityPersistenceBootstrap;
import pl.fejzu.persistence.velocity.sector.VelocitySectorIntegration;

@Plugin(
    id = "ffpersistence-example-velocity",
    name = "ffPersistence Example Velocity",
    version = "1.0.0",
    description = "ffPersistence usage example for Velocity",
    authors = {"fejzu"}
)
public final class ExampleVelocityPlugin {

    private static final ExampleLogger LOG = ExampleLogger.of(ExampleVelocityPlugin.class);

    private final ProxyServer proxy;

    private DefaultPersistenceService service;
    private SyncService syncService;
    private SectorService sectorService;
    private VelocityPersistenceBootstrap bootstrap;

    @Inject
    public ExampleVelocityPlugin(ProxyServer proxy) {
        this.proxy = proxy;
    }

    @Subscribe
    public void onProxyInitialize(ProxyInitializeEvent event) {
        service = ExamplePersistenceFactory.createVelocityService();

        ExampleUserRepository userRepository = service.repository(ExampleUserRepository.class);

        syncService = DefaultSyncService.builder()
            .context(service.getContext())
            .build();
        ExampleSyncFactory.registerAll(syncService);
        syncService.start();

        ExampleLastSectorProvider lastSectorProvider = new ExampleLastSectorProvider(userRepository);
        PersistenceSettings settings = ExampleSettingsFactory.sectorsEnabled(
            service.getContext().getSettings(), "lobby", "lobby-server"
        );

        sectorService = DefaultSectorService.builder()
            .context(service.getContext())
            .syncService(syncService)
            .build();
        ExampleSectorFactory.registerSectors(sectorService);
        sectorService.start();

        bootstrap = new VelocityPersistenceBootstrap(this, proxy, service)
            .register();

        VelocitySectorIntegration sectorIntegration = new VelocitySectorIntegration(
            proxy, this, sectorService, settings.getSector()
        );
        sectorIntegration.register();

        ExampleVelocityCommands commands = new ExampleVelocityCommands(
            proxy, syncService, sectorService, lastSectorProvider
        );
        proxy.getCommandManager().register(
            proxy.getCommandManager().metaBuilder("sendsector").plugin(this).build(),
            commands
        );
        proxy.getCommandManager().register(
            proxy.getCommandManager().metaBuilder("syncplayer").plugin(this).build(),
            commands
        );
        proxy.getCommandManager().register(
            proxy.getCommandManager().metaBuilder("connectlast").plugin(this).build(),
            commands
        );

        proxy.getEventManager().register(this,
            new ExampleVelocityListener(service, sectorService, lastSectorProvider)
        );

        LOG.info("ffPersistence example (Velocity) enabled");
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        if (bootstrap != null) {
            bootstrap.unregister();
        }
        if (syncService != null) {
            syncService.stop();
        }
        if (sectorService != null) {
            sectorService.stop();
        }
        if (service != null) {
            service.shutdown();
        }
        LOG.info("ffPersistence example (Velocity) disabled");
    }

    public ProxyServer getProxy() {
        return proxy;
    }

    public SectorService getSectorService() {
        return sectorService;
    }

    public SyncService getSyncService() {
        return syncService;
    }
}
