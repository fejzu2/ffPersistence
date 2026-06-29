package pl.fejzu.persistence.examples.paper;

import org.bukkit.plugin.java.JavaPlugin;
import pl.fejzu.persistence.core.service.DefaultPersistenceService;
import pl.fejzu.persistence.examples.common.factory.ExamplePersistenceFactory;
import pl.fejzu.persistence.examples.common.factory.ExampleSettingsFactory;
import pl.fejzu.persistence.examples.common.loader.ExamplePlayerLoader;
import pl.fejzu.persistence.examples.common.repository.ExampleUserRepository;
import pl.fejzu.persistence.examples.common.sector.ExampleLastSectorProvider;
import pl.fejzu.persistence.examples.common.sector.ExampleSectorFactory;
import pl.fejzu.persistence.examples.common.sync.ExampleSyncFactory;
import pl.fejzu.persistence.examples.paper.command.ExamplePaperCommands;
import pl.fejzu.persistence.examples.paper.listener.ExamplePaperListener;
import pl.fejzu.persistence.paper.PaperPersistenceBootstrap;
import pl.fejzu.persistence.sector.SectorService;
import pl.fejzu.persistence.sector.core.DefaultSectorService;
import pl.fejzu.persistence.sync.SyncService;
import pl.fejzu.persistence.sync.core.DefaultSyncService;

public final class ExamplePaperPlugin extends JavaPlugin {

    private DefaultPersistenceService service;
    private SyncService syncService;
    private SectorService sectorService;
    private PaperPersistenceBootstrap bootstrap;

    @Override
    public void onEnable() {
        service = ExamplePersistenceFactory.createPaperService();

        ExampleUserRepository userRepository = service.repository(ExampleUserRepository.class);

        syncService = DefaultSyncService.builder()
            .context(service.getContext())
            .build();
        ExampleSyncFactory.registerAll(syncService);
        syncService.start();

        ExampleLastSectorProvider lastSectorProvider = new ExampleLastSectorProvider(userRepository);
        sectorService = DefaultSectorService.builder()
            .context(service.getContext())
            .syncService(syncService)
            .build();
        ExampleSectorFactory.registerSectors(sectorService);
        sectorService.start();

        ExamplePlayerLoader playerLoader = new ExamplePlayerLoader(userRepository);

        bootstrap = new PaperPersistenceBootstrap(this, service)
            .playerLoader(playerLoader)
            .register();

        ExamplePaperCommands commands = new ExamplePaperCommands(service, syncService, sectorService);
        getCommand("coins").setExecutor(commands);
        getCommand("addcoins").setExecutor(commands);
        getCommand("sector").setExecutor(commands);
        getCommand("sync").setExecutor(commands);
        getCommand("save").setExecutor(commands);
        getCommand("load").setExecutor(commands);

        getServer().getPluginManager().registerEvents(
            new ExamplePaperListener(service, syncService, lastSectorProvider),
            this
        );

        getLogger().info("ffPersistence example (Paper) enabled");
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
        getLogger().info("ffPersistence example (Paper) disabled");
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
