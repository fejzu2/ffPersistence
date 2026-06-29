package pl.fejzu.persistence.messaging.codec;

import pl.fejzu.persistence.messaging.packet.ErrorPacket;
import pl.fejzu.persistence.messaging.packet.LoadPlayerPacket;
import pl.fejzu.persistence.messaging.packet.PlayerReadyPacket;
import pl.fejzu.persistence.messaging.packet.PlayerSavePacket;
import pl.fejzu.persistence.messaging.packet.PlayerSectorChangePacket;
import pl.fejzu.persistence.messaging.packet.PlayerTransferCompletePacket;
import pl.fejzu.persistence.messaging.packet.PlayerTransferFailedPacket;
import pl.fejzu.persistence.messaging.packet.PlayerTransferReadyPacket;
import pl.fejzu.persistence.messaging.packet.PlayerTransferStartPacket;
import pl.fejzu.persistence.messaging.packet.PlayerUnloadPacket;
import pl.fejzu.persistence.messaging.packet.PluginMessagingPacket;
import pl.fejzu.persistence.messaging.packet.PluginMessagingPacketType;

public final class PluginMessagingCodec {

    public PluginMessagingPacket decode(byte[] data) {
        if (data == null || data.length == 0) {
            throw new IllegalArgumentException("Packet data is empty");
        }
        PluginMessagingPacketType type = PluginMessagingPacketType.fromId(data[0]);
        return switch (type) {
            case LOAD_PLAYER -> LoadPlayerPacket.deserialize(data);
            case PLAYER_READY -> PlayerReadyPacket.deserialize(data);
            case PLAYER_UNLOAD -> PlayerUnloadPacket.deserialize(data);
            case PLAYER_SAVE -> PlayerSavePacket.deserialize(data);
            case PLAYER_SECTOR_CHANGE -> PlayerSectorChangePacket.deserialize(data);
            case PLAYER_TRANSFER_START -> PlayerTransferStartPacket.deserialize(data);
            case PLAYER_TRANSFER_READY -> PlayerTransferReadyPacket.deserialize(data);
            case PLAYER_TRANSFER_COMPLETE -> PlayerTransferCompletePacket.deserialize(data);
            case PLAYER_TRANSFER_FAILED -> PlayerTransferFailedPacket.deserialize(data);
            case ERROR -> ErrorPacket.deserialize(data);
        };
    }

    public byte[] encode(PluginMessagingPacket packet) {
        return packet.serialize();
    }
}
