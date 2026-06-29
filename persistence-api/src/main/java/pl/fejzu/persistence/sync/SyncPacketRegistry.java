package pl.fejzu.persistence.sync;

import java.util.Collection;
import java.util.Optional;

public interface SyncPacketRegistry {

    <T extends SyncPacket> void register(String typeId, Class<T> packetClass, SyncPacketCodec<T> codec, SyncPacketHandler<T> handler);

    Optional<SyncPacketCodec<?>> getCodec(String typeId);

    Optional<SyncPacketHandler<?>> getHandler(String typeId);

    Optional<Class<? extends SyncPacket>> getPacketClass(String typeId);

    boolean isRegistered(String typeId);

    Collection<String> getRegisteredTypes();
}
