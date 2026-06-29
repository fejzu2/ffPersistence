package pl.fejzu.persistence.sync;

public interface SyncPacketCodec<T extends SyncPacket> {

    byte[] encode(T packet);

    T decode(byte[] data);

    String getTypeId();
}
