package pl.fejzu.persistence.bungee.transfer;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import pl.fejzu.persistence.bungee.proxy.BungeeProxyBridge;
import pl.fejzu.persistence.bungee.queue.BungeeQueueService;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public final class BungeePlayerTransferService {

    private final ProxyServer proxy;
    private final BungeeProxyBridge proxyBridge;
    private final BungeeQueueService queueService;

    public BungeePlayerTransferService(
        ProxyServer proxy,
        BungeeProxyBridge proxyBridge,
        BungeeQueueService queueService
    ) {
        this.proxy = proxy;
        this.proxyBridge = proxyBridge;
        this.queueService = queueService;
    }

    public CompletableFuture<Void> transferPlayer(UUID playerId, String targetServerName) {
        ProxiedPlayer player = proxy.getPlayer(playerId);
        if (player == null) {
            return CompletableFuture.failedFuture(new IllegalStateException("Player not online: " + playerId));
        }
        ServerInfo targetServer = proxy.getServerInfo(targetServerName);
        if (targetServer == null) {
            return CompletableFuture.failedFuture(new IllegalStateException("Server not found: " + targetServerName));
        }
        return proxyBridge.requestPlayerLoad(playerId, targetServerName)
            .thenCompose(v -> queueService.waitForPlayerReady(playerId))
            .thenRun(() -> {
                ProxiedPlayer p = proxy.getPlayer(playerId);
                if (p != null) {
                    p.connect(targetServer);
                }
            });
    }
}
