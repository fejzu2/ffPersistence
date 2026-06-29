package pl.fejzu.persistence.sync.core;

import pl.fejzu.persistence.sync.SyncCustomRegistrationBuilder;
import pl.fejzu.persistence.sync.SyncPacket;
import pl.fejzu.persistence.sync.SyncPacketCodec;
import pl.fejzu.persistence.sync.SyncPacketHandler;
import pl.fejzu.persistence.sync.SyncPacketRegistry;

public final class DefaultSyncCustomRegistrationBuilder implements SyncCustomRegistrationBuilder {

    private final String typeId;
    private final SyncPacketRegistry registry;
    private Class<? extends SyncPacket> packetClass;
    private SyncPacketHandler<?> handler;
    private SyncPacketCodec<?> codec;

    DefaultSyncCustomRegistrationBuilder(String typeId, SyncPacketRegistry registry) {
        this.typeId = typeId;
        this.registry = registry;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends SyncPacket> SyncCustomRegistrationBuilder handler(Class<T> packetClass, SyncPacketHandler<T> handler) {
        this.packetClass = packetClass;
        this.handler = handler;
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends SyncPacket> SyncCustomRegistrationBuilder codec(Class<T> packetClass, SyncPacketCodec<T> codec) {
        this.packetClass = packetClass;
        this.codec = codec;
        return this;
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void register() {
        if (packetClass == null || handler == null || codec == null) {
            throw new IllegalStateException("typeId=" + typeId + " requires packetClass, handler and codec");
        }
        registry.register(typeId, (Class) packetClass, (SyncPacketCodec) codec, (SyncPacketHandler) handler);
    }
}
