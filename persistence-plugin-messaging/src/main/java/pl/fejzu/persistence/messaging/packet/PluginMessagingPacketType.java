package pl.fejzu.persistence.messaging.packet;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PluginMessagingPacketType {

    LOAD_PLAYER((byte) 0x01),
    PLAYER_READY((byte) 0x02),
    PLAYER_UNLOAD((byte) 0x03),
    PLAYER_SAVE((byte) 0x04),
    PLAYER_SECTOR_CHANGE((byte) 0x05),
    PLAYER_TRANSFER_START((byte) 0x06),
    PLAYER_TRANSFER_READY((byte) 0x07),
    PLAYER_TRANSFER_COMPLETE((byte) 0x08),
    PLAYER_TRANSFER_FAILED((byte) 0x09),
    ERROR((byte) 0xFF);

    private final byte id;

    public static PluginMessagingPacketType fromId(byte id) {
        for (PluginMessagingPacketType type : values()) {
            if (type.id == id) return type;
        }
        throw new IllegalArgumentException("Unknown packet type id: " + id);
    }
}
