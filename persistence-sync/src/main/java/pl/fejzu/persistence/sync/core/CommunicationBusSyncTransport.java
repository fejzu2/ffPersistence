package pl.fejzu.persistence.sync.core;

import pl.fejzu.persistence.communication.CommunicationBus;
import pl.fejzu.persistence.communication.CommunicationChannel;
import pl.fejzu.persistence.communication.GenericCommunicationMessage;
import pl.fejzu.persistence.sync.SyncPacket;
import pl.fejzu.persistence.sync.SyncPacketCodec;
import pl.fejzu.persistence.sync.SyncPacketRegistry;
import pl.fejzu.persistence.sync.SyncPacketType;
import pl.fejzu.persistence.sync.SyncTarget;
import pl.fejzu.persistence.sync.SyncTransport;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public final class CommunicationBusSyncTransport implements SyncTransport {

    private static final String CHANNEL_PLAYER = "ffpersistence:sync:player";
    private static final String CHANNEL_ENTITY = "ffpersistence:sync:entity";
    private static final String CHANNEL_GENERAL = "ffpersistence:sync";

    private final CommunicationBus bus;
    private final SyncPacketRegistry packetRegistry;
    private final Map<String, Consumer<pl.fejzu.persistence.communication.CommunicationMessage>> subscriptions = new ConcurrentHashMap<>();
    private volatile BiConsumer<String, byte[]> globalHandler;

    public CommunicationBusSyncTransport(CommunicationBus bus, SyncPacketRegistry packetRegistry) {
        this.bus = bus;
        this.packetRegistry = packetRegistry;
    }

    @Override
    @SuppressWarnings("unchecked")
    public CompletableFuture<Void> send(SyncPacket packet, SyncTarget target) {
        return CompletableFuture.runAsync(() -> {
            SyncPacketCodec<SyncPacket> codec = (SyncPacketCodec<SyncPacket>) packetRegistry.getCodec(packet.getType().name()).orElse(null);
            if (codec == null) return;
            byte[] data = codec.encode(packet);
            String channel = resolveChannel(packet.getType());
            bus.publish(CommunicationChannel.of(channel), GenericCommunicationMessage.of(packet.getType().name(), data));
        });
    }

    @Override
    @SuppressWarnings("unchecked")
    public CompletableFuture<Void> broadcast(SyncPacket packet) {
        return CompletableFuture.runAsync(() -> {
            SyncPacketCodec<SyncPacket> codec = (SyncPacketCodec<SyncPacket>) packetRegistry.getCodec(packet.getType().name()).orElse(null);
            if (codec == null) return;
            byte[] data = codec.encode(packet);
            String channel = resolveChannel(packet.getType());
            bus.publish(CommunicationChannel.of(channel), GenericCommunicationMessage.of(packet.getType().name(), data));
        });
    }

    @Override
    public void subscribeAll(BiConsumer<String, byte[]> handler) {
        this.globalHandler = handler;
        for (String channel : new String[]{CHANNEL_PLAYER, CHANNEL_ENTITY, CHANNEL_GENERAL}) {
            Consumer<pl.fejzu.persistence.communication.CommunicationMessage> listener = msg -> {
                if (globalHandler != null) {
                    globalHandler.accept(msg.getType(), msg.getPayload());
                }
            };
            subscriptions.put(channel, listener);
            bus.subscribe(CommunicationChannel.of(channel), listener);
        }
    }

    @Override
    public void unsubscribeAll() {
        for (String channel : subscriptions.keySet()) {
            bus.unsubscribe(CommunicationChannel.of(channel));
        }
        subscriptions.clear();
        globalHandler = null;
    }

    @Override
    public boolean isAvailable() {
        return bus != null && bus.isConnected();
    }

    private String resolveChannel(SyncPacketType type) {
        return switch (type) {
            case PLAYER_FULL_SYNC, PLAYER_FIELD_SYNC -> CHANNEL_PLAYER;
            case ENTITY_FULL_SYNC, ENTITY_FIELD_SYNC -> CHANNEL_ENTITY;
            default -> CHANNEL_GENERAL;
        };
    }
}
