package pl.fejzu.persistence.messaging.packet;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public final class PlayerUnloadPacket implements PluginMessagingPacket {

    private final UUID playerId;

    @Override
    public PluginMessagingPacketType getType() {
        return PluginMessagingPacketType.PLAYER_UNLOAD;
    }

    @Override
    public byte[] serialize() {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             DataOutputStream dos = new DataOutputStream(bos)) {
            dos.writeByte(getType().getId());
            dos.writeLong(playerId.getMostSignificantBits());
            dos.writeLong(playerId.getLeastSignificantBits());
            return bos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Failed to serialize PlayerUnloadPacket", e);
        }
    }

    public static PlayerUnloadPacket deserialize(byte[] data) {
        try (java.io.ByteArrayInputStream bis = new java.io.ByteArrayInputStream(data);
             java.io.DataInputStream dis = new java.io.DataInputStream(bis)) {
            dis.readByte();
            UUID playerId = new UUID(dis.readLong(), dis.readLong());
            return new PlayerUnloadPacket(playerId);
        } catch (IOException e) {
            throw new RuntimeException("Failed to deserialize PlayerUnloadPacket", e);
        }
    }
}
