package pl.fejzu.persistence.bungee.proxy;

import net.md_5.bungee.api.ProxyServer;
import pl.fejzu.persistence.bungee.messaging.BungeePluginMessagingBridge;
import pl.fejzu.persistence.messaging.packet.LoadPlayerPacket;
import pl.fejzu.persistence.messaging.packet.PlayerReadyPacket;
import pl.fejzu.persistence.messaging.packet.PlayerSavePacket;
import pl.fejzu.persistence.messaging.packet.PlayerUnloadPacket;
import pl.fejzu.persistence.proxy.ProxyBridge;
import pl.fejzu.persistence.proxy.ProxyPlatform;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public final class BungeeProxyBridge implements ProxyBridge {

    private final ProxyServer proxy;
    private final BungeePluginMessagingBridge messagingBridge;

    public BungeeProxyBridge(ProxyServer proxy, BungeePluginMessagingBridge messagingBridge) {
        this.proxy = proxy;
        this.messagingBridge = messagingBridge;
    }

    @Override
    public CompletableFuture<Void> requestPlayerLoad(UUID playerId, String targetServer) {
        return CompletableFuture.runAsync(() -> {
            net.md_5.bungee.api.connection.ProxiedPlayer player = proxy.getPlayer(playerId);
            if (player == null) return;
            messagingBridge.sendPacket(
                "ffpersistence:load",
                new LoadPlayerPacket(playerId, player.getName(), targetServer)
            );
        });
    }

    @Override
    public CompletableFuture<Void> notifyPlayerReady(UUID playerId) {
        return CompletableFuture.runAsync(() -> {
            net.md_5.bungee.api.connection.ProxiedPlayer player = proxy.getPlayer(playerId);
            String serverName = player != null && player.getServer() != null
                ? player.getServer().getInfo().getName()
                : "unknown";
            messagingBridge.sendPacket("ffpersistence:ready", new PlayerReadyPacket(playerId, serverName));
        });
    }

    @Override
    public CompletableFuture<Void> notifyPlayerUnload(UUID playerId) {
        return CompletableFuture.runAsync(() ->
            messagingBridge.sendPacket("ffpersistence:main", new PlayerUnloadPacket(playerId))
        );
    }

    @Override
    public CompletableFuture<Void> notifyPlayerSave(UUID playerId) {
        return CompletableFuture.runAsync(() ->
            messagingBridge.sendPacket("ffpersistence:save", new PlayerSavePacket(playerId))
        );
    }

    @Override
    public ProxyPlatform getPlatform() {
        return ProxyPlatform.BUNGEECORD;
    }

    @Override
    public boolean isConnected() {
        return true;
    }
}
