package pl.fejzu.persistence.sync;

public interface SyncCustomRegistrationBuilder {

    <T extends SyncPacket> SyncCustomRegistrationBuilder handler(Class<T> packetClass, SyncPacketHandler<T> handler);

    <T extends SyncPacket> SyncCustomRegistrationBuilder codec(Class<T> packetClass, SyncPacketCodec<T> codec);

    void register();
}
