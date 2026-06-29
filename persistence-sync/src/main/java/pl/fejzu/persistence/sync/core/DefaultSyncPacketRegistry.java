package pl.fejzu.persistence.sync.core;

import pl.fejzu.persistence.sync.SyncPacket;
import pl.fejzu.persistence.sync.SyncPacketCodec;
import pl.fejzu.persistence.sync.SyncPacketHandler;
import pl.fejzu.persistence.sync.SyncPacketRegistry;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class DefaultSyncPacketRegistry implements SyncPacketRegistry {

    private final Map<String, SyncPacketCodec<?>> codecs = new ConcurrentHashMap<>();
    private final Map<String, SyncPacketHandler<?>> handlers = new ConcurrentHashMap<>();
    private final Map<String, Class<? extends SyncPacket>> packetClasses = new ConcurrentHashMap<>();

    @Override
    public <T extends SyncPacket> void register(String typeId, Class<T> packetClass, SyncPacketCodec<T> codec, SyncPacketHandler<T> handler) {
        packetClasses.put(typeId, packetClass);
        codecs.put(typeId, codec);
        handlers.put(typeId, handler);
    }

    @Override
    public Optional<SyncPacketCodec<?>> getCodec(String typeId) {
        return Optional.ofNullable(codecs.get(typeId));
    }

    @Override
    public Optional<SyncPacketHandler<?>> getHandler(String typeId) {
        return Optional.ofNullable(handlers.get(typeId));
    }

    @Override
    public Optional<Class<? extends SyncPacket>> getPacketClass(String typeId) {
        return Optional.ofNullable(packetClasses.get(typeId));
    }

    @Override
    public boolean isRegistered(String typeId) {
        return codecs.containsKey(typeId);
    }

    @Override
    public Collection<String> getRegisteredTypes() {
        return codecs.keySet();
    }
}
