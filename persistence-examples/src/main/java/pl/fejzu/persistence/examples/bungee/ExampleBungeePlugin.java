package pl.fejzu.persistence.examples.bungee;

import net.md_5.bungee.api.plugin.Plugin;
import pl.fejzu.persistence.bungee.BungeePersistenceBootstrap;
import pl.fejzu.persistence.bungee.sector.BungeeSectorIntegration;
import pl.fejzu.persistence.core.service.DefaultPersistenceService;
import pl.fejzu.persistence.examples.common.factory.ExampleLogger;
import pl.fejzu.persistence.examples.common.factory.ExamplePersistenceFactory;
import pl.fejzu.persistence.examples.common.factory.ExampleSettingsFactory;
import pl.fejzu.persistence.examples.common.repository.ExampleUserRepository;
import pl.fejzu.persistence.examples.common.sector.ExampleLastSectorProvider;
import pl.fejzu.persistence.examples.common.sector.ExampleSectorFactory;
import pl.fejzu.persistence.examples.common.sync.ExampleSyncFactory;
import pl.fejzu.persistence.examples.bungee.command.ExampleBungeeCommands;
import pl.fejzu.persistence.examples.bungee.listener.ExampleBungeeListener;
import pl.fejzu.persistence.sector.SectorService;
import pl.fejzu.persistence.sector.core.DefaultSectorService;
import pl.fejzu.persistence.settings.PersistenceSettings;
import pl.fejzu.persistence.sync.SyncService;
import pl.fejzu.persistence.sync.core.DefaultSyncService;

public final class ExampleBungeePlugin extends Plugin {

    private static final ExampleLogger LOG = ExampleLogger.of(ExampleBungeePlugin.class);

    private DefaultPersistenceService service;
    private SyncService syncService;
    private SectorService sectorService;
    private BungeePersistenceBootstrap bootstrap;

    @Override
    public void onEnable() {
        service = ExamplePersistenceFactory.createBungeeService();

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

        bootstrap = new BungeePersistenceBootstrap(this, service)
            .register();

        BungeeSectorIntegration sectorIntegration = new BungeeSectorIntegration(
            this, sectorService, settings.getSector()
        );
        sectorIntegration.register();

        ExampleBungeeCommands.create(this, syncService, sectorService, lastSectorProvider)
            .forEach(cmd -> getProxy().getPluginManager().registerCommand(this, cmd));

        getProxy().getPluginManager().registerListener(this,
            new ExampleBungeeListener(service, sectorService, lastSectorProvider)
        );

        LOG.info("ffPersistence example (BungeeCord) enabled");
    }

    @Override
    public void onDisable() {
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
        LOG.info("ffPersistence example (BungeeCord) disabled");
    }

    public DefaultPersistenceService getService() {
        return service;
    }

    public SyncService getSyncService() {
        return syncService;
    }

    public SectorService getSectorService() {
        return sectorService;
    }
}
