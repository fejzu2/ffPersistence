package pl.fejzu.persistence.service;

import pl.fejzu.persistence.cache.CacheProvider;
import pl.fejzu.persistence.communication.CommunicationBus;
import pl.fejzu.persistence.sector.SectorService;
import pl.fejzu.persistence.settings.PersistenceSettings;
import pl.fejzu.persistence.storage.StorageProvider;
import pl.fejzu.persistence.sync.PlayerSyncService;
import pl.fejzu.persistence.sync.SyncService;

import java.util.Optional;
import java.util.concurrent.Executor;

public interface PersistenceContext {

    StorageProvider getStorageProvider();

    CacheProvider getCacheProvider();

    CommunicationBus getCommunicationBus();

    Executor getExecutor();

    PersistenceRegistry getRegistry();

    PersistenceSettings getSettings();

    Optional<SyncService> getSyncService();

    Optional<SectorService> getSectorService();

    default Optional<PlayerSyncService> getPlayerSyncService() {
        return getSyncService().map(SyncService::playerSync);
    }
}
