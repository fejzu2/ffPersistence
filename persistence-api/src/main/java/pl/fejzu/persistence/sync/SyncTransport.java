package pl.fejzu.persistence.sync;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public interface SyncTransport {

    CompletableFuture<Void> send(SyncPacket packet, SyncTarget target);

    CompletableFuture<Void> broadcast(SyncPacket packet);

    void subscribeAll(BiConsumer<String, byte[]> handler);

    void unsubscribeAll();

    boolean isAvailable();
}
