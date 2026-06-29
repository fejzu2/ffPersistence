package pl.fejzu.persistence.messaging.packet;

public interface PluginMessagingPacket {

    PluginMessagingPacketType getType();

    byte[] serialize();
}
