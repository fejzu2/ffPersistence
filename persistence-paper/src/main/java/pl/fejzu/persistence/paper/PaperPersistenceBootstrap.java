package pl.fejzu.persistence.paper;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import pl.fejzu.persistence.core.lifecycle.LifecycleService;
import pl.fejzu.persistence.paper.display.PaperActionBarLoadingDisplay;
import pl.fejzu.persistence.paper.display.PaperBossBarLoadingDisplay;
import pl.fejzu.persistence.paper.display.PaperLoadingAnimationTask;
import pl.fejzu.persistence.paper.display.PaperLoadingDisplay;
import pl.fejzu.persistence.paper.listener.PaperPlayerLifecycleListener;
import pl.fejzu.persistence.paper.messaging.PaperPluginMessagingBridge;
import pl.fejzu.persistence.paper.platform.PaperPlatformAdapter;
import pl.fejzu.persistence.paper.scheduler.PaperSchedulerAdapter;
import pl.fejzu.persistence.player.PlayerDataLoader;
import pl.fejzu.persistence.service.PersistenceService;
import pl.fejzu.persistence.settings.LoadingSettings;
import pl.fejzu.persistence.settings.PersistenceSettings;

public final class PaperPersistenceBootstrap {

    private final JavaPlugin plugin;
    private final PersistenceService service;

    private PlayerDataLoader playerLoader;

    @Getter
    private PaperPluginMessagingBridge messagingBridge;
    @Getter
    private PaperLoadingAnimationTask animationTask;
    @Getter
    private final PaperPlatformAdapter platformAdapter;
    @Getter
    private final PaperSchedulerAdapter schedulerAdapter;

    private final LifecycleService lifecycleService = new LifecycleService();

    public PaperPersistenceBootstrap(JavaPlugin plugin, PersistenceService service) {
        this.plugin = plugin;
        this.service = service;
        this.platformAdapter = new PaperPlatformAdapter(plugin);
        this.schedulerAdapter = new PaperSchedulerAdapter(plugin);
    }

    public PaperPersistenceBootstrap playerLoader(PlayerDataLoader loader) {
        this.playerLoader = loader;
        return this;
    }

    public PaperPersistenceBootstrap register() {
        lifecycleService.register(service);
        lifecycleService.start();

        PersistenceSettings settings = service.getContext().getSettings();

        if (playerLoader != null) {
            PaperPlayerLifecycleListener listener =
                new PaperPlayerLifecycleListener(playerLoader, settings.getPlayerLifecycle());
            plugin.getServer().getPluginManager().registerEvents(listener, plugin);
        }

        messagingBridge = new PaperPluginMessagingBridge(plugin);
        messagingBridge.register();

        LoadingSettings loadingSettings = settings.getLoading();
        if (loadingSettings.isEnabled() && playerLoader != null) {
            PaperLoadingDisplay display = createDisplay(loadingSettings);
            animationTask = new PaperLoadingAnimationTask(
                plugin,
                display,
                loadingSettings.getAnimation(),
                loadingSettings.getTitle(),
                loadingSettings.getAnimationIntervalMs()
            );
        }

        return this;
    }

    public void unregister() {
        if (messagingBridge != null) {
            messagingBridge.unregister();
        }
        lifecycleService.shutdown();
    }

    private PaperLoadingDisplay createDisplay(LoadingSettings settings) {
        return switch (settings.getDisplayType()) {
            case ACTION_BAR -> new PaperActionBarLoadingDisplay(plugin.getServer());
            default -> new PaperBossBarLoadingDisplay(plugin.getServer(), settings);
        };
    }
}
