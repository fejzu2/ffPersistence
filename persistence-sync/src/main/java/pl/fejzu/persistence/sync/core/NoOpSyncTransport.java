package pl.fejzu.persistence.sync.core;

import pl.fejzu.persistence.sync.SyncPacket;
import pl.fejzu.persistence.sync.SyncTarget;
import pl.fejzu.persistence.sync.SyncTransport;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public final class NoOpSyncTransport implements SyncTransport {

    @Override
    public CompletableFuture<Void> send(SyncPacket packet, SyncTarget target) {
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<Void> broadcast(SyncPacket packet) {
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public void subscribeAll(BiConsumer<String, byte[]> handler) {
    }

    @Override
    public void unsubscribeAll() {
    }

    @Override
    public boolean isAvailable() {
        return false;
    }
}
