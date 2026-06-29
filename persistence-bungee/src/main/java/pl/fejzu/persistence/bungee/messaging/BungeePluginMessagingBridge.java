package pl.fejzu.persistence.bungee.messaging;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;
import pl.fejzu.persistence.messaging.PluginMessagingBus;
import pl.fejzu.persistence.messaging.channel.PluginMessagingChannel;
import pl.fejzu.persistence.messaging.codec.PluginMessagingCodec;
import pl.fejzu.persistence.messaging.packet.PluginMessagingPacket;
import pl.fejzu.persistence.messaging.packet.PluginMessagingPacketType;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public final class BungeePluginMessagingBridge implements PluginMessagingBus, Listener {

    private final Plugin plugin;
    private final ProxyServer proxy;
    private final PluginMessagingCodec codec = new PluginMessagingCodec();
    private final Map<PluginMessagingPacketType, Consumer<PluginMessagingPacket>> handlers = new ConcurrentHashMap<>();

    public BungeePluginMessagingBridge(Plugin plugin) {
        this.plugin = plugin;
        this.proxy = plugin.getProxy();
    }

    public void register() {
        proxy.registerChannel(PluginMessagingChannel.MAIN);
        proxy.registerChannel(PluginMessagingChannel.LOAD);
        proxy.registerChannel(PluginMessagingChannel.READY);
        proxy.getPluginManager().registerListener(plugin, this);
    }

    public void unregister() {
        proxy.unregisterChannel(PluginMessagingChannel.MAIN);
        proxy.unregisterChannel(PluginMessagingChannel.LOAD);
        proxy.unregisterChannel(PluginMessagingChannel.READY);
    }

    @EventHandler
    public void onPluginMessage(PluginMessageEvent event) {
        String channel = event.getTag();
        if (!channel.startsWith("ffpersistence:")) return;
        event.setCancelled(true);
        handleIncoming(channel, event.getData());
    }

    @Override
    public void sendPacket(String channel, PluginMessagingPacket packet) {
        proxy.getPlayers().stream().findFirst().ifPresent(player -> {
            player.getServer().sendData(channel, codec.encode(packet));
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
