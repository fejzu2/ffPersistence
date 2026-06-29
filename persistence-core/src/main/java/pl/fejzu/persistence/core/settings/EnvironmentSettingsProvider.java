package pl.fejzu.persistence.core.settings;

import pl.fejzu.persistence.settings.*;

import java.time.Duration;

public final class EnvironmentSettingsProvider implements SettingsProvider {

    private static final String PREFIX = "FFPERSISTENCE_";

    private final SettingsProvider base;

    public EnvironmentSettingsProvider(SettingsProvider base) {
        this.base = base;
    }

    public EnvironmentSettingsProvider() {
        this(new DefaultSettingsProvider());
    }

    @Override
    public PersistenceSettings provide() {
        PersistenceSettings settings = base.provide();
        settings = applyCache(settings);
        settings = applyLoading(settings);
        settings = applyPlayerLifecycle(settings);
        settings = applyProxy(settings);
        settings = applyQueue(settings);
        settings = applySync(settings);
        settings = applyStorage(settings);
        settings = applyCommunication(settings);
        settings = applySector(settings);
        return settings.withSource(SettingsSource.ENVIRONMENT);
    }

    @Override
    public SettingsSource getSource() {
        return SettingsSource.ENVIRONMENT;
    }

    // --- cache ---

    private PersistenceSettings applyCache(PersistenceSettings s) {
        CacheSettings cache = s.getCache();
        String enabled = env("CACHE_ENABLED");
        String expireWrite = env("CACHE_EXPIRE_AFTER_WRITE_MINUTES");
        String expireAccess = env("CACHE_EXPIRE_AFTER_ACCESS_MINUTES");
        String maxSize = env("CACHE_MAX_SIZE");
        String saveOnEvict = env("CACHE_SAVE_ON_EVICT");
        String cleanup = env("CACHE_CLEANUP_INTERVAL_MINUTES");
        if (enabled == null && expireWrite == null && expireAccess == null
            && maxSize == null && saveOnEvict == null && cleanup == null) {
            return s;
        }
        CacheSettings.CacheSettingsBuilder b = cache.toBuilder();
        if (enabled != null) b.enabled(Boolean.parseBoolean(enabled));
        if (expireWrite != null) b.expireAfterWrite(Duration.ofMinutes(Long.parseLong(expireWrite)));
        if (expireAccess != null) b.expireAfterAccess(Duration.ofMinutes(Long.parseLong(expireAccess)));
        if (maxSize != null) b.maxSize(Long.parseLong(maxSize));
        if (saveOnEvict != null) b.saveOnEvict(Boolean.parseBoolean(saveOnEvict));
        if (cleanup != null) b.cleanupInterval(Duration.ofMinutes(Long.parseLong(cleanup)));
        return s.withCache(b.build());
    }

    // --- loading ---

    private PersistenceSettings applyLoading(PersistenceSettings s) {
        LoadingSettings loading = s.getLoading();
        String enabled = env("LOADING_ENABLED");
        String title = env("LOADING_TITLE");
        String displayType = env("LOADING_DISPLAY_TYPE");
        String timeout = env("LOADING_TIMEOUT_SECONDS");
        String intervalMs = env("LOADING_ANIMATION_INTERVAL_MS");
        String hideOnSuccess = env("LOADING_HIDE_ON_SUCCESS");
        if (enabled == null && title == null && displayType == null
            && timeout == null && intervalMs == null && hideOnSuccess == null) {
            return s;
        }
        LoadingSettings.LoadingSettingsBuilder b = loading.toBuilder();
        if (enabled != null) b.enabled(Boolean.parseBoolean(enabled));
        if (title != null) b.title(title);
        if (displayType != null) b.displayType(LoadingDisplayType.valueOf(displayType.toUpperCase()));
        if (timeout != null) b.loadTimeoutSeconds(Long.parseLong(timeout));
        if (intervalMs != null) b.animationIntervalMs(Long.parseLong(intervalMs));
        if (hideOnSuccess != null) b.hideOnSuccess(Boolean.parseBoolean(hideOnSuccess));
        return s.withLoading(b.build());
    }

    // --- playerLifecycle ---

    private PersistenceSettings applyPlayerLifecycle(PersistenceSettings s) {
        PlayerLifecycleSettings lc = s.getPlayerLifecycle();
        String loadOnJoin = env("LIFECYCLE_LOAD_ON_JOIN");
        String saveOnQuit = env("LIFECYCLE_SAVE_ON_QUIT");
        String unloadOnQuit = env("LIFECYCLE_UNLOAD_ON_QUIT");
        String saveOnShutdown = env("LIFECYCLE_SAVE_ON_SHUTDOWN");
        String saveIntervalMinutes = env("LIFECYCLE_SAVE_INTERVAL_MINUTES");
        String loadTimeout = env("LIFECYCLE_LOAD_TIMEOUT_SECONDS");
        String kickOnFailure = env("LIFECYCLE_KICK_ON_LOAD_FAILURE");
        String kickMessage = env("LIFECYCLE_KICK_MESSAGE");
        if (loadOnJoin == null && saveOnQuit == null && unloadOnQuit == null
            && saveOnShutdown == null && saveIntervalMinutes == null && loadTimeout == null
            && kickOnFailure == null && kickMessage == null) {
            return s;
        }
        PlayerLifecycleSettings.PlayerLifecycleSettingsBuilder b = lc.toBuilder();
        if (loadOnJoin != null) b.loadOnJoin(Boolean.parseBoolean(loadOnJoin));
        if (saveOnQuit != null) b.saveOnQuit(Boolean.parseBoolean(saveOnQuit));
        if (unloadOnQuit != null) b.unloadOnQuit(Boolean.parseBoolean(unloadOnQuit));
        if (saveOnShutdown != null) b.saveOnShutdown(Boolean.parseBoolean(saveOnShutdown));
        if (saveIntervalMinutes != null) b.saveInterval(Duration.ofMinutes(Long.parseLong(saveIntervalMinutes)));
        if (loadTimeout != null) b.loadTimeoutSeconds(Long.parseLong(loadTimeout));
        if (kickOnFailure != null) b.kickOnLoadFailure(Boolean.parseBoolean(kickOnFailure));
        if (kickMessage != null) b.kickMessage(kickMessage);
        return s.withPlayerLifecycle(b.build());
    }

    // --- proxy ---

    private PersistenceSettings applyProxy(PersistenceSettings s) {
        ProxySettings proxy = s.getProxy();
        String enabled = env("PROXY_ENABLED");
        String waitForReady = env("PROXY_WAIT_FOR_BACKEND_READY");
        String transferTimeout = env("PROXY_TRANSFER_TIMEOUT_SECONDS");
        String fallback = env("PROXY_FALLBACK_SERVER");
        if (enabled == null && waitForReady == null && transferTimeout == null && fallback == null) {
            return s;
        }
        ProxySettings.ProxySettingsBuilder b = proxy.toBuilder();
        if (enabled != null) b.enabled(Boolean.parseBoolean(enabled));
        if (waitForReady != null) b.waitForBackendReady(Boolean.parseBoolean(waitForReady));
        if (transferTimeout != null) b.transferTimeoutSeconds(Long.parseLong(transferTimeout));
        if (fallback != null) b.fallbackServer(fallback);
        return s.withProxy(b.build());
    }

    // --- queue ---

    private PersistenceSettings applyQueue(PersistenceSettings s) {
        QueueSettings queue = s.getQueue();
        String enabled = env("QUEUE_ENABLED");
        String showPosition = env("QUEUE_SHOW_POSITION");
        String preload = env("QUEUE_PRELOAD_WHEN_FIRST");
        String message = env("QUEUE_LOADING_MESSAGE");
        if (enabled == null && showPosition == null && preload == null && message == null) return s;
        QueueSettings.QueueSettingsBuilder b = queue.toBuilder();
        if (enabled != null) b.enabled(Boolean.parseBoolean(enabled));
        if (showPosition != null) b.showPosition(Boolean.parseBoolean(showPosition));
        if (preload != null) b.preloadWhenFirst(Boolean.parseBoolean(preload));
        if (message != null) b.loadingMessage(message);
        return s.withQueue(b.build());
    }

    // --- sync ---

    private PersistenceSettings applySync(PersistenceSettings s) {
        SyncSettings sync = s.getSync();
        String enabled = env("SYNC_ENABLED");
        String debounce = env("SYNC_DEBOUNCE_DELAY_MS");
        String batchSeconds = env("SYNC_BATCH_INTERVAL_SECONDS");
        if (enabled == null && debounce == null && batchSeconds == null) return s;
        SyncSettings.SyncSettingsBuilder b = sync.toBuilder();
        if (enabled != null) b.enabled(Boolean.parseBoolean(enabled));
        if (debounce != null) b.debounceDelayMs(Long.parseLong(debounce));
        if (batchSeconds != null) b.batchInterval(Duration.ofSeconds(Long.parseLong(batchSeconds)));
        return s.withSync(b.build());
    }

    // --- storage ---

    private PersistenceSettings applyStorage(PersistenceSettings s) {
        StorageSettings storage = s.getStorage();
        String connectTimeout = env("STORAGE_CONNECT_TIMEOUT_MS");
        String socketTimeout = env("STORAGE_SOCKET_TIMEOUT_MS");
        String maxPool = env("STORAGE_MAX_POOL_SIZE");
        String maxRetries = env("STORAGE_MAX_RETRIES");
        String retryDelay = env("STORAGE_RETRY_DELAY_MS");
        if (connectTimeout == null && socketTimeout == null && maxPool == null
            && maxRetries == null && retryDelay == null) {
            return s;
        }
        StorageSettings.StorageSettingsBuilder b = storage.toBuilder();
        if (connectTimeout != null) b.connectTimeoutMs(Integer.parseInt(connectTimeout));
        if (socketTimeout != null) b.socketTimeoutMs(Integer.parseInt(socketTimeout));
        if (maxPool != null) b.maxPoolSize(Integer.parseInt(maxPool));
        if (maxRetries != null) b.maxRetries(Integer.parseInt(maxRetries));
        if (retryDelay != null) b.retryDelayMs(Long.parseLong(retryDelay));
        return s.withStorage(b.build());
    }

    // --- communication ---

    private PersistenceSettings applyCommunication(PersistenceSettings s) {
        CommunicationSettings comm = s.getCommunication();
        String enabled = env("COMMUNICATION_ENABLED");
        String debug = env("COMMUNICATION_DEBUG_PACKETS");
        String prefix = env("COMMUNICATION_CHANNELS_PREFIX");
        String timeout = env("COMMUNICATION_REQUEST_TIMEOUT_MS");
        if (enabled == null && debug == null && prefix == null && timeout == null) return s;
        CommunicationSettings.CommunicationSettingsBuilder b = comm.toBuilder();
        if (enabled != null) b.enabled(Boolean.parseBoolean(enabled));
        if (debug != null) b.debugPackets(Boolean.parseBoolean(debug));
        if (prefix != null) b.channelsPrefix(prefix);
        if (timeout != null) b.requestTimeoutMs(Long.parseLong(timeout));
        return s.withCommunication(b.build());
    }

    // --- sector ---

    private PersistenceSettings applySector(PersistenceSettings s) {
        SectorSettings sector = s.getSector();
        String enabled = env("SECTOR_ENABLED");
        String current = env("SECTOR_CURRENT");
        String timeout = env("SECTOR_TRANSFER_TIMEOUT_SECONDS");
        String lock = env("SECTOR_LOCK_PLAYER_DATA");
        if (enabled == null && current == null && timeout == null && lock == null) return s;
        SectorSettings.SectorSettingsBuilder b = sector.toBuilder();
        if (enabled != null) b.enabled(Boolean.parseBoolean(enabled));
        if (current != null) b.currentSector(current);
        if (timeout != null) b.transferTimeoutSeconds(Long.parseLong(timeout));
        if (lock != null) b.lockPlayerDataDuringTransfer(Boolean.parseBoolean(lock));
        return s.withSector(b.build());
    }

    private String env(String key) {
        return System.getenv(PREFIX + key);
    }
}
