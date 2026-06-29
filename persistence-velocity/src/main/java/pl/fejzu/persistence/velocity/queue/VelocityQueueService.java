package pl.fejzu.persistence.velocity.queue;

import com.velocitypowered.api.proxy.ProxyServer;
import pl.fejzu.persistence.messaging.packet.PluginMessagingPacket;
import pl.fejzu.persistence.messaging.packet.PluginMessagingPacketType;
import pl.fejzu.persistence.messaging.packet.PlayerReadyPacket;
import pl.fejzu.persistence.settings.ProxySettings;
import pl.fejzu.persistence.velocity.messaging.VelocityPluginMessagingBridge;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public final class VelocityQueueService {

    private static final Logger LOGGER = Logger.getLogger(VelocityQueueService.class.getName());

    private final ProxyServer proxy;
    private final VelocityPluginMessagingBridge messagingBridge;
    private final ProxySettings settings;
    private final Map<UUID, CompletableFuture<Void>> pendingReadyFutures = new ConcurrentHashMap<>();

    public VelocityQueueService(
        ProxyServer proxy,
        VelocityPluginMessagingBridge messagingBridge,
        ProxySettings settings
    ) {
        this.proxy = proxy;
        this.messagingBridge = messagingBridge;
        this.settings = settings;
        messagingBridge.registerHandler(PluginMessagingPacketType.PLAYER_READY, this::onPlayerReady);
    }

    public VelocityQueueService(ProxyServer proxy, VelocityPluginMessagingBridge messagingBridge) {
        this(proxy, messagingBridge, ProxySettings.defaults());
    }

    public CompletableFuture<Void> waitForPlayerReady(UUID playerId) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        pendingReadyFutures.put(playerId, future);

        long timeoutSeconds = settings.getTransferTimeoutSeconds();
        CompletableFuture.delayedExecutor(timeoutSeconds, TimeUnit.SECONDS).execute(() -> {
            CompletableFuture<Void> pending = pendingReadyFutures.remove(playerId);
            if (pending != null && !pending.isDone()) {
                pending.completeExceptionally(
                    new RuntimeException("Player ready timeout after " + timeoutSeconds + "s for: " + playerId)
                );
            }
        });

        return future;
    }

    private void onPlayerReady(PluginMessagingPacket packet) {
        if (!(packet instanceof PlayerReadyPacket readyPacket)) return;
        UUID playerId = readyPacket.getPlayerId();
        CompletableFuture<Void> future = pendingReadyFutures.remove(playerId);
        if (future != null) {
            future.complete(null);
        }
    }

    public boolean isWaitingForReady(UUID playerId) {
        return pendingReadyFutures.containsKey(playerId);
    }
}
