package pl.fejzu.persistence.core.service;

import lombok.Builder;
import lombok.Getter;
import pl.fejzu.persistence.cache.CacheProvider;
import pl.fejzu.persistence.communication.CommunicationBus;
import pl.fejzu.persistence.core.cache.MemoryCacheProvider;
import pl.fejzu.persistence.core.executor.PersistenceExecutor;
import pl.fejzu.persistence.sector.SectorService;
import pl.fejzu.persistence.service.PersistenceContext;
import pl.fejzu.persistence.service.PersistenceRegistry;
import pl.fejzu.persistence.settings.PersistenceSettings;
import pl.fejzu.persistence.storage.StorageProvider;
import pl.fejzu.persistence.sync.SyncService;

import java.util.Optional;
import java.util.concurrent.Executor;

@Getter
@Builder
public final class DefaultPersistenceContext implements PersistenceContext {

    private final StorageProvider storageProvider;
    @Builder.Default
    private final CacheProvider cacheProvider = new MemoryCacheProvider();
    private final CommunicationBus communicationBus;
    @Builder.Default
    private final PersistenceExecutor persistenceExecutor = new PersistenceExecutor();
    private final PersistenceRegistry registry;
    @Builder.Default
    private final PersistenceSettings settings = PersistenceSettings.defaults();
    private final SyncService syncService;
    private final SectorService sectorService;

    @Override
    public Executor getExecutor() {
        return persistenceExecutor.getExecutorService();
    }

    @Override
    public Optional<SyncService> getSyncService() {
        return Optional.ofNullable(syncService);
    }

    @Override
    public Optional<SectorService> getSectorService() {
        return Optional.ofNullable(sectorService);
    }
}
