package pl.fejzu.persistence.velocity.proxy;

import com.velocitypowered.api.proxy.ProxyServer;
import pl.fejzu.persistence.messaging.packet.LoadPlayerPacket;
import pl.fejzu.persistence.messaging.packet.PlayerReadyPacket;
import pl.fejzu.persistence.messaging.packet.PlayerSavePacket;
import pl.fejzu.persistence.messaging.packet.PlayerUnloadPacket;
import pl.fejzu.persistence.proxy.ProxyBridge;
import pl.fejzu.persistence.proxy.ProxyPlatform;
import pl.fejzu.persistence.velocity.messaging.VelocityPluginMessagingBridge;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public final class VelocityProxyBridge implements ProxyBridge {

    private final ProxyServer proxy;
    private final VelocityPluginMessagingBridge messagingBridge;

    public VelocityProxyBridge(ProxyServer proxy, VelocityPluginMessagingBridge messagingBridge) {
        this.proxy = proxy;
        this.messagingBridge = messagingBridge;
    }

    @Override
    public CompletableFuture<Void> requestPlayerLoad(UUID playerId, String targetServer) {
        return CompletableFuture.runAsync(() -> {
            proxy.getPlayer(playerId).ifPresent(player -> {
                String playerName = player.getUsername();
                messagingBridge.sendPacket(
                    "ffpersistence:load",
                    new LoadPlayerPacket(playerId, playerName, targetServer)
                );
            });
        });
    }

    @Override
    public CompletableFuture<Void> notifyPlayerReady(UUID playerId) {
        return CompletableFuture.runAsync(() -> {
            String serverName = proxy.getPlayer(playerId)
                .flatMap(p -> p.getCurrentServer())
                .map(s -> s.getServerInfo().getName())
                .orElse("unknown");
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
        return ProxyPlatform.VELOCITY;
    }

    @Override
    public boolean isConnected() {
        return true;
    }
}
