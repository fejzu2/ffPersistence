package pl.fejzu.persistence.sector.core;

import pl.fejzu.persistence.sector.LastSectorProvider;
import pl.fejzu.persistence.sector.LastSectorResolver;
import pl.fejzu.persistence.sector.Sector;
import pl.fejzu.persistence.sector.SectorId;
import pl.fejzu.persistence.sector.SectorLockService;
import pl.fejzu.persistence.sector.SectorPlayerTracker;
import pl.fejzu.persistence.sector.SectorPreloadHandler;
import pl.fejzu.persistence.sector.SectorRegistry;
import pl.fejzu.persistence.sector.SectorService;
import pl.fejzu.persistence.sector.SectorSyncBridge;
import pl.fejzu.persistence.sector.SectorTransfer;
import pl.fejzu.persistence.service.PersistenceContext;
import pl.fejzu.persistence.settings.SectorSettings;
import pl.fejzu.persistence.sync.SyncService;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public final class DefaultSectorService implements SectorService {

    private final PersistenceContext context;
    private final DefaultSectorRegistry registry;
    private final DefaultSectorPlayerTracker playerTracker;
    private final DefaultSectorLockService lockService;
    private final DefaultSectorPreloadHandler preloadHandler;
    private final SectorSyncBridge syncBridge;
    private final SectorPacketDispatcher packetDispatcher;
    private final SectorTransferCoordinator transferCoordinator;
    private final SectorPlayerDataPreloader preloader;
    private final AtomicBoolean running = new AtomicBoolean(false);

    private DefaultSectorService(Builder builder) {
        this.context = builder.context;
        SectorSettings settings = context.getSettings().getSector();

        this.registry = new DefaultSectorRegistry();
        this.playerTracker = new DefaultSectorPlayerTracker();
        this.lockService = new DefaultSectorLockService();
        this.preloadHandler = new DefaultSectorPreloadHandler();
        this.preloader = new SectorPlayerDataPreloader(preloadHandler);

        SyncService syncService = builder.syncService != null
            ? builder.syncService
            : context.getSyncService().orElse(null);

        this.syncBridge = syncService != null ? new DefaultSectorSyncBridge(syncService) : null;

        boolean hasBus = context.getCommunicationBus() != null && context.getCommunicationBus().isConnected();
        this.packetDispatcher = hasBus
            ? new SectorPacketDispatcher(context.getCommunicationBus(), preloader, settings.getCurrentServerName())
            : null;

        DefaultSectorTransferHandler handler = new DefaultSectorTransferHandler(
            registry, playerTracker, lockService, preloadHandler, syncBridge,
            packetDispatcher, settings, context.getExecutor()
        );
        this.transferCoordinator = new SectorTransferCoordinator(handler);
    }

    public DefaultSectorService register() {
        start();
        return this;
    }

    @Override
    public void start() {
        if (!running.compareAndSet(false, true)) return;
        if (packetDispatcher != null) {
            packetDispatcher.registerHandlers();
        }
    }

    @Override
    public void stop() {
        running.set(false);
    }

    @Override
    public boolean isRunning() {
        return running.get();
    }

    @Override
    public void registerSector(Sector sector) {
        registry.register(sector);
    }

    @Override
    public Optional<Sector> getSector(SectorId id) {
        return registry.getSector(id);
    }

    @Override
    public SectorRegistry registry() {
        return registry;
    }

    @Override
    public SectorPlayerTracker playerTracker() {
        return playerTracker;
    }

    @Override
    public SectorLockService lockService() {
        return lockService;
    }

    @Override
    public SectorTransfer transfer(UUID playerId) {
        return new DefaultSectorTransfer(playerId, transferCoordinator, playerTracker);
    }

    public SectorTransferCoordinator getCoordinator() {
        return transferCoordinator;
    }

    public SectorPreloadHandler getPreloadHandler() {
        return preloadHandler;
    }

    public DefaultSectorConnectService buildConnectService(LastSectorProvider lastSectorProvider) {
        SectorSettings settings = context.getSettings().getSector();
        LastSectorResolver resolver = new DefaultLastSectorResolver(lastSectorProvider, registry, settings);
        SectorAvailabilityService availability = new SectorAvailabilityService(registry);
        SectorSelectionService selection = new SectorSelectionService(resolver, availability);
        return new DefaultSectorConnectService(selection, registry, lastSectorProvider, settings);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private PersistenceContext context;
        private SyncService syncService;

        public Builder context(PersistenceContext context) {
            this.context = context;
            return this;
        }

        public Builder syncService(SyncService syncService) {
            this.syncService = syncService;
            return this;
        }

        public DefaultSectorService build() {
            if (context == null) throw new IllegalStateException("PersistenceContext is required");
            return new DefaultSectorService(this);
        }
    }
}
