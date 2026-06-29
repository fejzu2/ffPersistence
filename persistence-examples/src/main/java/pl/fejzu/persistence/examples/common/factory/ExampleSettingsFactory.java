package pl.fejzu.persistence.examples.common.factory;

import pl.fejzu.persistence.sector.SectorJoinStrategy;
import pl.fejzu.persistence.settings.CacheSettings;
import pl.fejzu.persistence.settings.CommunicationSettings;
import pl.fejzu.persistence.settings.LoadingSettings;
import pl.fejzu.persistence.settings.PersistenceSettings;
import pl.fejzu.persistence.settings.PlayerLifecycleSettings;
import pl.fejzu.persistence.settings.ProxySettings;
import pl.fejzu.persistence.settings.SectorSettings;
import pl.fejzu.persistence.settings.SyncSettings;

public final class ExampleSettingsFactory {

    private ExampleSettingsFactory() {}

    public static PersistenceSettings paper() {
        return PersistenceSettings.builder()
            .loading(LoadingSettings.defaults())
            .cache(CacheSettings.aggressive())
            .playerLifecycle(PlayerLifecycleSettings.defaults())
            .sync(SyncSettings.enabled())
            .proxy(ProxySettings.defaults())
            .sector(SectorSettings.defaults())
            .build();
    }

    public static PersistenceSettings velocity() {
        return PersistenceSettings.builder()
            .loading(LoadingSettings.disabled())
            .cache(CacheSettings.defaults())
            .playerLifecycle(PlayerLifecycleSettings.noAutoSave())
            .sync(SyncSettings.enabled())
            .proxy(ProxySettings.velocity())
            .sector(SectorSettings.defaults())
            .build();
    }

    public static PersistenceSettings bungee() {
        return PersistenceSettings.builder()
            .loading(LoadingSettings.disabled())
            .cache(CacheSettings.defaults())
            .playerLifecycle(PlayerLifecycleSettings.noAutoSave())
            .sync(SyncSettings.enabled())
            .proxy(ProxySettings.bungeeCord())
            .sector(SectorSettings.defaults())
            .build();
    }

    public static PersistenceSettings withRedis(PersistenceSettings base) {
        return base.withCommunication(
            CommunicationSettings.builder()
                .enabled(true)
                .defaultBus("redis")
                .build()
        );
    }

    public static PersistenceSettings withNats(PersistenceSettings base) {
        return base.withCommunication(
            CommunicationSettings.builder()
                .enabled(true)
                .defaultBus("nats")
                .build()
        );
    }

    public static PersistenceSettings sectorsEnabled(PersistenceSettings base, String currentSector, String serverName) {
        return base.withSector(
            SectorSettings.builder()
                .enabled(true)
                .currentSector(currentSector)
                .currentServerName(serverName)
                .connectToLastSectorOnJoin(true)
                .saveLastSectorOnQuit(true)
                .fallbackSector("lobby")
                .lobbySector("lobby")
                .preloadTarget(true)
                .lockPlayerDataDuringTransfer(true)
                .fullSyncOnTransfer(true)
                .proxyAutoConnectEnabled(true)
                .joinStrategy(SectorJoinStrategy.LAST_SECTOR)
                .allowFallbackWhenLastOffline(true)
                .build()
        );
    }
}
