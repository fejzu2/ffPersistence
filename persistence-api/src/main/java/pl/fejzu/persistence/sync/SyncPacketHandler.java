package pl.fejzu.persistence.sync;

@FunctionalInterface
public interface SyncPacketHandler<T extends SyncPacket> {

    void handle(T packet, SyncContext context);
}
