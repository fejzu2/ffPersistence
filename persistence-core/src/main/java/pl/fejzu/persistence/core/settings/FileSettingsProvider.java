package pl.fejzu.persistence.core.settings;

import pl.fejzu.persistence.settings.*;
import pl.fejzu.persistence.sync.SyncConflictStrategy;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Properties;
import java.util.logging.Logger;

public final class FileSettingsProvider implements SettingsProvider {

    private static final Logger LOGGER = Logger.getLogger(FileSettingsProvider.class.getName());

    private final Path configFile;
    private final SettingsProvider base;

    public FileSettingsProvider(Path configFile, SettingsProvider base) {
        this.configFile = configFile;
        this.base = base;
    }

    public FileSettingsProvider(Path configFile) {
        this(configFile, new DefaultSettingsProvider());
    }

    @Override
    public PersistenceSettings provide() {
        if (!isAvailable()) {
            return base.provide();
        }

        Properties props = load();
        PersistenceSettings settings = base.provide();

        settings = applyCache(settings, props);
        settings = applyLoading(settings, props);
        settings = applyPlayerLifecycle(settings, props);
        settings = applyProxy(settings, props);
        settings = applyQueue(settings, props);
        settings = applySync(settings, props);
        settings = applyStorage(settings, props);
        settings = applyCommunication(settings, props);
        settings = applySector(settings, props);

        return settings.withSource(SettingsSource.FILE);
    }

    @Override
    public SettingsSource getSource() {
        return SettingsSource.FILE;
    }

    @Override
    public boolean isAvailable() {
        return Files.exists(configFile) && Files.isReadable(configFile);
    }

    private Properties load() {
        Properties props = new Properties();
        try (InputStream in = Files.newInputStream(configFile)) {
            props.load(in);
        } catch (IOException e) {
            LOGGER.warning("Failed to read settings file " + configFile + ": " + e.getMessage());
        }
        return props;
    }

    private PersistenceSettings applyCache(PersistenceSettings s, Properties p) {
        if (!hasAny(p, "cache.")) return s;
        CacheSettings.CacheSettingsBuilder b = s.getCache().toBuilder();
        str(p, "cache.enabled", v -> b.enabled(Boolean.parseBoolean(v)));
        str(p, "cache.expire-after-write-minutes", v -> b.expireAfterWrite(Duration.ofMinutes(Long.parseLong(v))));
        str(p, "cache.expire-after-access-minutes", v -> b.expireAfterAccess(Duration.ofMinutes(Long.parseLong(v))));
        str(p, "cache.max-size", v -> b.maxSize(Long.parseLong(v)));
        str(p, "cache.save-on-evict", v -> b.saveOnEvict(Boolean.parseBoolean(v)));
        str(p, "cache.cleanup-interval-minutes", v -> b.cleanupInterval(Duration.ofMinutes(Long.parseLong(v))));
        return s.withCache(b.build());
    }

    private PersistenceSettings applyLoading(PersistenceSettings s, Properties p) {
        if (!hasAny(p, "loading.")) return s;
        LoadingSettings.LoadingSettingsBuilder b = s.getLoading().toBuilder();
        str(p, "loading.enabled", v -> b.enabled(Boolean.parseBoolean(v)));
        str(p, "loading.title", b::title);
        str(p, "loading.subtitle", b::subtitle);
        str(p, "loading.display-type", v -> b.displayType(LoadingDisplayType.valueOf(v.toUpperCase())));
        str(p, "loading.boss-bar-color", v -> b.bossBarColor(BossBarColor.valueOf(v.toUpperCase())));
        str(p, "loading.boss-bar-style", v -> b.bossBarStyle(BossBarStyle.valueOf(v.toUpperCase())));
        str(p, "loading.animation-interval-ms", v -> b.animationIntervalMs(Long.parseLong(v)));
        str(p, "loading.timeout-seconds", v -> b.loadTimeoutSeconds(Long.parseLong(v)));
        str(p, "loading.hide-on-success", v -> b.hideOnSuccess(Boolean.parseBoolean(v)));
        applyLoadingMessages(s, p, b);
        return s.withLoading(b.build());
    }

    private void applyLoadingMessages(PersistenceSettings s, Properties p, LoadingSettings.LoadingSettingsBuilder lb) {
        if (!hasAny(p, "loading.message.")) return;
        LoadingMessages.LoadingMessagesBuilder mb = s.getLoading().getMessages().toBuilder();
        str(p, "loading.message.loading", mb::loading);
        str(p, "loading.message.success", mb::success);
        str(p, "loading.message.error", mb::error);
        str(p, "loading.message.timeout", mb::timeout);
        str(p, "loading.message.kick", mb::kick);
        str(p, "loading.message.transferring", mb::transferring);
        lb.messages(mb.build());
    }

    private PersistenceSettings applyPlayerLifecycle(PersistenceSettings s, Properties p) {
        if (!hasAny(p, "lifecycle.")) return s;
        PlayerLifecycleSettings.PlayerLifecycleSettingsBuilder b = s.getPlayerLifecycle().toBuilder();
        str(p, "lifecycle.load-on-join", v -> b.loadOnJoin(Boolean.parseBoolean(v)));
        str(p, "lifecycle.save-on-quit", v -> b.saveOnQuit(Boolean.parseBoolean(v)));
        str(p, "lifecycle.unload-on-quit", v -> b.unloadOnQuit(Boolean.parseBoolean(v)));
        str(p, "lifecycle.save-on-shutdown", v -> b.saveOnShutdown(Boolean.parseBoolean(v)));
        str(p, "lifecycle.save-interval-minutes", v -> b.saveInterval(Duration.ofMinutes(Long.parseLong(v))));
        str(p, "lifecycle.load-timeout-seconds", v -> b.loadTimeoutSeconds(Long.parseLong(v)));
        str(p, "lifecycle.kick-on-load-failure", v -> b.kickOnLoadFailure(Boolean.parseBoolean(v)));
        str(p, "lifecycle.kick-message", b::kickMessage);
        return s.withPlayerLifecycle(b.build());
    }

    private PersistenceSettings applyProxy(PersistenceSettings s, Properties p) {
        if (!hasAny(p, "proxy.")) return s;
        ProxySettings.ProxySettingsBuilder b = s.getProxy().toBuilder();
        str(p, "proxy.enabled", v -> b.enabled(Boolean.parseBoolean(v)));
        str(p, "proxy.wait-for-backend-ready", v -> b.waitForBackendReady(Boolean.parseBoolean(v)));
        str(p, "proxy.transfer-timeout-seconds", v -> b.transferTimeoutSeconds(Long.parseLong(v)));
        str(p, "proxy.fallback-server", b::fallbackServer);
        return s.withProxy(b.build());
    }

    private PersistenceSettings applyQueue(PersistenceSettings s, Properties p) {
        if (!hasAny(p, "queue.")) return s;
        QueueSettings.QueueSettingsBuilder b = s.getQueue().toBuilder();
        str(p, "queue.enabled", v -> b.enabled(Boolean.parseBoolean(v)));
        str(p, "queue.show-position", v -> b.showPosition(Boolean.parseBoolean(v)));
        str(p, "queue.preload-when-first", v -> b.preloadWhenFirst(Boolean.parseBoolean(v)));
        str(p, "queue.loading-message", b::loadingMessage);
        return s.withQueue(b.build());
    }

    private PersistenceSettings applySync(PersistenceSettings s, Properties p) {
        if (!hasAny(p, "sync.")) return s;
        SyncSettings.SyncSettingsBuilder b = s.getSync().toBuilder();
        str(p, "sync.enabled", v -> b.enabled(Boolean.parseBoolean(v)));
        str(p, "sync.debounce-delay-ms", v -> b.debounceDelayMs(Long.parseLong(v)));
        str(p, "sync.batch-interval-seconds", v -> b.batchInterval(Duration.ofSeconds(Long.parseLong(v))));
        str(p, "sync.default-strategy", v -> b.defaultStrategy(SyncStrategy.valueOf(v.toUpperCase())));
        str(p, "sync.conflict-strategy", v -> b.conflictStrategy(SyncConflictStrategy.valueOf(v.toUpperCase())));
        return s.withSync(b.build());
    }

    private PersistenceSettings applyStorage(PersistenceSettings s, Properties p) {
        if (!hasAny(p, "storage.")) return s;
        StorageSettings.StorageSettingsBuilder b = s.getStorage().toBuilder();
        str(p, "storage.connect-timeout-ms", v -> b.connectTimeoutMs(Integer.parseInt(v)));
        str(p, "storage.socket-timeout-ms", v -> b.socketTimeoutMs(Integer.parseInt(v)));
        str(p, "storage.max-pool-size", v -> b.maxPoolSize(Integer.parseInt(v)));
        str(p, "storage.max-retries", v -> b.maxRetries(Integer.parseInt(v)));
        str(p, "storage.retry-delay-ms", v -> b.retryDelayMs(Long.parseLong(v)));
        return s.withStorage(b.build());
    }

    private PersistenceSettings applyCommunication(PersistenceSettings s, Properties p) {
        if (!hasAny(p, "communication.")) return s;
        CommunicationSettings.CommunicationSettingsBuilder b = s.getCommunication().toBuilder();
        str(p, "communication.enabled", v -> b.enabled(Boolean.parseBoolean(v)));
        str(p, "communication.debug-packets", v -> b.debugPackets(Boolean.parseBoolean(v)));
        str(p, "communication.channels-prefix", b::channelsPrefix);
        str(p, "communication.request-timeout-ms", v -> b.requestTimeoutMs(Long.parseLong(v)));
        return s.withCommunication(b.build());
    }

    private PersistenceSettings applySector(PersistenceSettings s, Properties p) {
        if (!hasAny(p, "sector.")) return s;
        SectorSettings.SectorSettingsBuilder b = s.getSector().toBuilder();
        str(p, "sector.enabled", v -> b.enabled(Boolean.parseBoolean(v)));
        str(p, "sector.current", b::currentSector);
        str(p, "sector.transfer-timeout-seconds", v -> b.transferTimeoutSeconds(Long.parseLong(v)));
        str(p, "sector.preload-target", v -> b.preloadTarget(Boolean.parseBoolean(v)));
        str(p, "sector.lock-player-data", v -> b.lockPlayerDataDuringTransfer(Boolean.parseBoolean(v)));
        return s.withSector(b.build());
    }

    private boolean hasAny(Properties p, String prefix) {
        return p.stringPropertyNames().stream().anyMatch(k -> k.startsWith(prefix));
    }

    private void str(Properties p, String key, java.util.function.Consumer<String> action) {
        String value = p.getProperty(key);
        if (value != null && !value.isBlank()) {
            try {
                action.accept(value.trim());
            } catch (Exception e) {
                LOGGER.warning("Invalid value for '" + key + "': " + value);
            }
        }
    }
}
