package pl.fejzu.persistence.velocity.transfer;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import pl.fejzu.persistence.velocity.proxy.VelocityProxyBridge;
import pl.fejzu.persistence.velocity.queue.VelocityQueueService;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

public final class VelocityPlayerTransferService {

    private static final Logger LOGGER = Logger.getLogger(VelocityPlayerTransferService.class.getName());

    private final ProxyServer proxy;
    private final VelocityProxyBridge proxyBridge;
    private final VelocityQueueService queueService;

    public VelocityPlayerTransferService(
        ProxyServer proxy,
        VelocityProxyBridge proxyBridge,
        VelocityQueueService queueService
    ) {
        this.proxy = proxy;
        this.proxyBridge = proxyBridge;
        this.queueService = queueService;
    }

    public CompletableFuture<Void> transferPlayer(UUID playerId, String targetServerName) {
        Optional<Player> player = proxy.getPlayer(playerId);
        if (player.isEmpty()) {
            return CompletableFuture.failedFuture(new IllegalStateException("Player not online: " + playerId));
        }

        Optional<RegisteredServer> targetServer = proxy.getServer(targetServerName);
        if (targetServer.isEmpty()) {
            return CompletableFuture.failedFuture(new IllegalStateException("Server not registered: " + targetServerName));
        }

        return proxyBridge.requestPlayerLoad(playerId, targetServerName)
            .thenCompose(v -> queueService.waitForPlayerReady(playerId))
            .thenCompose(v -> {
                Player p = proxy.getPlayer(playerId).orElseThrow();
                return p.createConnectionRequest(targetServer.get()).connect()
                    .thenApply(result -> (Void) null);
            });
    }
}
