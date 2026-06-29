package pl.fejzu.persistence.core.service;

import pl.fejzu.persistence.cache.CacheProvider;
import pl.fejzu.persistence.communication.CommunicationBus;
import pl.fejzu.persistence.core.cache.CachePolicy;
import pl.fejzu.persistence.core.cache.MemoryCacheProvider;
import pl.fejzu.persistence.core.executor.PersistenceExecutor;
import pl.fejzu.persistence.core.settings.DefaultSettingsProvider;
import pl.fejzu.persistence.entity.PersistenceEntity;
import pl.fejzu.persistence.repository.PersistenceRepository;
import pl.fejzu.persistence.sector.SectorService;
import pl.fejzu.persistence.service.PersistenceContext;
import pl.fejzu.persistence.service.PersistenceRegistry;
import pl.fejzu.persistence.service.PersistenceService;
import pl.fejzu.persistence.settings.CacheSettings;
import pl.fejzu.persistence.settings.PersistenceSettings;
import pl.fejzu.persistence.settings.SettingsProvider;
import pl.fejzu.persistence.storage.StorageProvider;
import pl.fejzu.persistence.sync.SyncService;

public final class DefaultPersistenceService implements PersistenceService {

    private final PersistenceContext context;

    private DefaultPersistenceService(PersistenceContext context) {
        this.context = context;
    }

    @Override
    public <T extends PersistenceEntity<ID>, ID, R extends PersistenceRepository<T, ID>>
    R repository(Class<R> repositoryClass) {
        return context.getRegistry().getOrCreate(repositoryClass, context);
    }

    @Override
    public PersistenceContext getContext() {
        return context;
    }

    @Override
    public void shutdown() {
        if (context.getStorageProvider() != null) {
            context.getStorageProvider().disconnect();
        }
        if (context.getCommunicationBus() != null && context.getCommunicationBus().isConnected()) {
            context.getCommunicationBus().disconnect();
        }
        context.getSyncService().ifPresent(SyncService::stop);
        context.getSectorService().ifPresent(SectorService::stop);
        if (context instanceof DefaultPersistenceContext ctx) {
            ctx.getPersistenceExecutor().shutdown();
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private StorageProvider storageProvider;
        private CacheProvider cacheProvider;
        private CommunicationBus communicationBus;
        private int workerThreads = Runtime.getRuntime().availableProcessors() * 2;
        private PersistenceSettings settings;
        private SettingsProvider settingsProvider;
        private SyncService syncService;
        private SectorService sectorService;

        public Builder storage(StorageProvider storageProvider) {
            this.storageProvider = storageProvider;
            return this;
        }

        public Builder cache(CacheProvider cacheProvider) {
            this.cacheProvider = cacheProvider;
            return this;
        }

        public Builder communication(CommunicationBus communicationBus) {
            this.communicationBus = communicationBus;
            return this;
        }

        public Builder workerThreads(int workerThreads) {
            this.workerThreads = workerThreads;
            return this;
        }

        public Builder settings(PersistenceSettings settings) {
            this.settings = settings;
            return this;
        }

        public Builder settingsProvider(SettingsProvider settingsProvider) {
            this.settingsProvider = settingsProvider;
            return this;
        }

        public Builder sync(SyncService syncService) {
            this.syncService = syncService;
            return this;
        }

        public Builder sector(SectorService sectorService) {
            this.sectorService = sectorService;
            return this;
        }

        public DefaultPersistenceService build() {
            PersistenceSettings resolvedSettings = resolveSettings();

            PersistenceRegistry registry = new DefaultPersistenceRegistry();
            PersistenceExecutor executor = new PersistenceExecutor(workerThreads);
            CacheProvider resolvedCache = resolveCacheProvider(resolvedSettings.getCache());

            PersistenceContext ctx = DefaultPersistenceContext.builder()
                .storageProvider(storageProvider)
                .cacheProvider(resolvedCache)
                .communicationBus(communicationBus)
                .persistenceExecutor(executor)
                .registry(registry)
                .settings(resolvedSettings)
                .syncService(syncService)
                .sectorService(sectorService)
                .build();

            return new DefaultPersistenceService(ctx);
        }

        private PersistenceSettings resolveSettings() {
            if (settingsProvider != null) {
                return settingsProvider.provide();
            }
            if (settings != null) {
                return settings;
            }
            return new DefaultSettingsProvider().provide();
        }

        private CacheProvider resolveCacheProvider(CacheSettings cacheSettings) {
            if (cacheProvider != null) {
                return cacheProvider;
            }
            if (!cacheSettings.isEnabled()) {
                return new MemoryCacheProvider(CachePolicy.builder().maxSize(0).build());
            }
            CachePolicy policy = CachePolicy.builder()
                .expireAfterWrite(cacheSettings.getExpireAfterWrite())
                .expireAfterAccess(cacheSettings.getExpireAfterAccess())
                .maxSize(cacheSettings.getMaxSize())
                .build();
            return new MemoryCacheProvider(policy);
        }
    }
}
