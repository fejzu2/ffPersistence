package pl.fejzu.persistence.velocity.messaging;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import pl.fejzu.persistence.messaging.PluginMessagingBus;
import pl.fejzu.persistence.messaging.channel.PluginMessagingChannel;
import pl.fejzu.persistence.messaging.codec.PluginMessagingCodec;
import pl.fejzu.persistence.messaging.packet.PluginMessagingPacket;
import pl.fejzu.persistence.messaging.packet.PluginMessagingPacketType;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public final class VelocityPluginMessagingBridge implements PluginMessagingBus {

    private static final MinecraftChannelIdentifier MAIN_CHANNEL =
        MinecraftChannelIdentifier.create("ffpersistence", "main");
    private static final MinecraftChannelIdentifier READY_CHANNEL =
        MinecraftChannelIdentifier.create("ffpersistence", "ready");

    private final ProxyServer proxy;
    private final PluginMessagingCodec codec = new PluginMessagingCodec();
    private final Map<PluginMessagingPacketType, Consumer<PluginMessagingPacket>> handlers = new ConcurrentHashMap<>();

    public VelocityPluginMessagingBridge(ProxyServer proxy) {
        this.proxy = proxy;
        proxy.getChannelRegistrar().register(MAIN_CHANNEL, READY_CHANNEL);
    }

    @Subscribe
    public void onPluginMessage(PluginMessageEvent event) {
        if (!event.getIdentifier().equals(MAIN_CHANNEL) && !event.getIdentifier().equals(READY_CHANNEL)) return;
        event.setResult(PluginMessageEvent.ForwardResult.handled());
        handleIncoming(event.getIdentifier().getId(), event.getData());
    }

    @Override
    public void sendPacket(String channel, PluginMessagingPacket packet) {
        byte[] data = codec.encode(packet);
        proxy.getAllPlayers().stream().findFirst().ifPresent(player -> {
            player.getCurrentServer().ifPresent(serverConnection -> {
                serverConnection.sendPluginMessage(
                    MinecraftChannelIdentifier.create(
                        channel.split(":")[0],
                        channel.split(":")[1]
                    ),
                    data
                );
            });
        });
    }

    @Override
    public void registerHandler(PluginMessagingPacketType type, Consumer<PluginMessagingPacket> handler) {
        handlers.put(type, handler);
    }

    @Override
    public void unregisterHandler(PluginMessagingPacketType type) {
        handlers.remove(type);
    }

    @Override
    public void handleIncoming(String channel, byte[] data) {
        PluginMessagingPacket packet = codec.decode(data);
        Consumer<PluginMessagingPacket> handler = handlers.get(packet.getType());
        if (handler != null) {
            handler.accept(packet);
        }
    }
}
