package pl.fejzu.persistence.sync.core;

import pl.fejzu.persistence.service.PersistenceContext;
import pl.fejzu.persistence.settings.SyncSettings;
import pl.fejzu.persistence.sync.PlayerSyncService;
import pl.fejzu.persistence.sync.SyncCustomRegistrationBuilder;
import pl.fejzu.persistence.sync.SyncPacket;
import pl.fejzu.persistence.sync.SyncPacketCodec;
import pl.fejzu.persistence.sync.SyncPacketHandler;
import pl.fejzu.persistence.sync.SyncPacketRegistry;
import pl.fejzu.persistence.sync.SyncRegistry;
import pl.fejzu.persistence.sync.SyncService;
import pl.fejzu.persistence.sync.SyncTarget;
import pl.fejzu.persistence.sync.SyncTransport;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

public final class DefaultSyncService implements SyncService {

    private final PersistenceContext context;
    private final DefaultSyncRegistry registry;
    private final DefaultSyncPacketRegistry packetRegistry;
    private final DefaultPlayerSyncService playerSyncService;
    private final SyncTransport transport;
    private final SyncPacketDispatcher dispatcher;
    private final AtomicBoolean running = new AtomicBoolean(false);

    private DefaultSyncService(Builder builder) {
        this.context = builder.context;
        this.registry = new DefaultSyncRegistry();
        this.packetRegistry = new DefaultSyncPacketRegistry();

        SyncSettings settings = context.getSettings().getSync();

        boolean hasBus = context.getCommunicationBus() != null && context.getCommunicationBus().isConnected();
        this.transport = hasBus
            ? new CommunicationBusSyncTransport(context.getCommunicationBus(), packetRegistry)
            : new NoOpSyncTransport();

        ReflectionSyncSnapshotProvider snapshotProvider = new ReflectionSyncSnapshotProvider(registry);
        DefaultSyncConflictResolver conflictResolver = new DefaultSyncConflictResolver(settings.getConflictStrategy());
        this.playerSyncService = new DefaultPlayerSyncService(this, snapshotProvider, conflictResolver, settings);
        this.dispatcher = new SyncPacketDispatcher(packetRegistry, this);
    }

    public DefaultSyncService register() {
        start();
        return this;
    }

    @Override
    public void start() {
        if (!running.compareAndSet(false, true)) return;
        transport.subscribeAll(dispatcher::dispatch);
    }

    @Override
    public void stop() {
        if (!running.compareAndSet(true, false)) return;
        transport.unsubscribeAll();
    }

    @Override
    public boolean isRunning() {
        return running.get();
    }

    @Override
    public SyncRegistry registry() {
        return registry;
    }

    @Override
    public SyncPacketRegistry packetRegistry() {
        return packetRegistry;
    }

    @Override
    public PlayerSyncService playerSync() {
        return playerSyncService;
    }

    @Override
    public SyncTransport transport() {
        return transport;
    }

    @Override
    public <T extends SyncPacket> void registerPacket(String typeId, Class<T> packetClass, SyncPacketCodec<T> codec, SyncPacketHandler<T> handler) {
        packetRegistry.register(typeId, packetClass, codec, handler);
    }

    @Override
    public SyncCustomRegistrationBuilder custom(String typeId) {
        return new DefaultSyncCustomRegistrationBuilder(typeId, packetRegistry);
    }

    @Override
    public CompletableFuture<Void> send(SyncPacket packet, SyncTarget target) {
        return transport.send(packet, target);
    }

    @Override
    public CompletableFuture<Void> broadcast(SyncPacket packet) {
        return transport.broadcast(packet);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private PersistenceContext context;

        public Builder context(PersistenceContext context) {
            this.context = context;
            return this;
        }

        public DefaultSyncService build() {
            if (context == null) throw new IllegalStateException("PersistenceContext is required");
            return new DefaultSyncService(this);
        }
    }
}
