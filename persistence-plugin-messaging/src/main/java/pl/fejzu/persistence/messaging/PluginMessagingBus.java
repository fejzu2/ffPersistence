package pl.fejzu.persistence.messaging;

import pl.fejzu.persistence.messaging.packet.PluginMessagingPacket;
import pl.fejzu.persistence.messaging.packet.PluginMessagingPacketType;

import java.util.function.Consumer;

public interface PluginMessagingBus {

    void sendPacket(String channel, PluginMessagingPacket packet);

    void registerHandler(PluginMessagingPacketType type, Consumer<PluginMessagingPacket> handler);

    void unregisterHandler(PluginMessagingPacketType type);

    void handleIncoming(String channel, byte[] data);
}
