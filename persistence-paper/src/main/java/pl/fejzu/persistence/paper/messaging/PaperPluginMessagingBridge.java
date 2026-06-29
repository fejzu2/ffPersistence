package pl.fejzu.persistence.paper.messaging;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import pl.fejzu.persistence.messaging.PluginMessagingBus;
import pl.fejzu.persistence.messaging.channel.PluginMessagingChannel;
import pl.fejzu.persistence.messaging.codec.PluginMessagingCodec;
import pl.fejzu.persistence.messaging.packet.PluginMessagingPacket;
import pl.fejzu.persistence.messaging.packet.PluginMessagingPacketType;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public final class PaperPluginMessagingBridge implements PluginMessagingBus, PluginMessageListener {

    private final JavaPlugin plugin;
    private final PluginMessagingCodec codec = new PluginMessagingCodec();
    private final Map<PluginMessagingPacketType, Consumer<PluginMessagingPacket>> handlers = new ConcurrentHashMap<>();

    public PaperPluginMessagingBridge(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void register() {
        plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, PluginMessagingChannel.MAIN);
        plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, PluginMessagingChannel.LOAD);
        plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, PluginMessagingChannel.READY);
        plugin.getServer().getMessenger().registerIncomingPluginChannel(plugin, PluginMessagingChannel.MAIN, this);
        plugin.getServer().getMessenger().registerIncomingPluginChannel(plugin, PluginMessagingChannel.READY, this);
    }

    public void unregister() {
        plugin.getServer().getMessenger().unregisterOutgoingPluginChannel(plugin);
        plugin.getServer().getMessenger().unregisterIncomingPluginChannel(plugin);
    }

    @Override
    public void sendPacket(String channel, PluginMessagingPacket packet) {
        Player onlinePlayer = plugin.getServer().getOnlinePlayers().stream().findFirst().orElse(null);
        if (onlinePlayer == null) return;
        onlinePlayer.sendPluginMessage(plugin, channel, codec.encode(packet));
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

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        handleIncoming(channel, message);
    }
}
