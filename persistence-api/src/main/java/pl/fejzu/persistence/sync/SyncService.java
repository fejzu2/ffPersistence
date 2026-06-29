package pl.fejzu.persistence.sync;

import java.util.concurrent.CompletableFuture;

public interface SyncService {

    SyncRegistry registry();

    SyncPacketRegistry packetRegistry();

    PlayerSyncService playerSync();

    SyncTransport transport();

    <T extends SyncPacket> void registerPacket(String typeId, Class<T> packetClass, SyncPacketCodec<T> codec, SyncPacketHandler<T> handler);

    SyncCustomRegistrationBuilder custom(String typeId);

    CompletableFuture<Void> send(SyncPacket packet, SyncTarget target);

    CompletableFuture<Void> broadcast(SyncPacket packet);

    void start();

    void stop();

    boolean isRunning();
}
