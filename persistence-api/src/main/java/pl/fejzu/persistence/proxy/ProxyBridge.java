package pl.fejzu.persistence.proxy;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface ProxyBridge {

    CompletableFuture<Void> requestPlayerLoad(UUID playerId, String targetServer);

    CompletableFuture<Void> notifyPlayerReady(UUID playerId);

    CompletableFuture<Void> notifyPlayerUnload(UUID playerId);

    CompletableFuture<Void> notifyPlayerSave(UUID playerId);

    ProxyPlatform getPlatform();

    boolean isConnected();
}
