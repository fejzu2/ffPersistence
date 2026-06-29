package pl.fejzu.persistence.messaging.packet;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public final class PlayerReadyPacket implements PluginMessagingPacket {

    private final UUID playerId;
    private final String serverName;

    @Override
    public PluginMessagingPacketType getType() {
        return PluginMessagingPacketType.PLAYER_READY;
    }

    @Override
    public byte[] serialize() {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             DataOutputStream dos = new DataOutputStream(bos)) {
            dos.writeByte(getType().getId());
            dos.writeLong(playerId.getMostSignificantBits());
            dos.writeLong(playerId.getLeastSignificantBits());
            dos.writeUTF(serverName);
            return bos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Failed to serialize PlayerReadyPacket", e);
        }
    }

    public static PlayerReadyPacket deserialize(byte[] data) {
        try (java.io.ByteArrayInputStream bis = new java.io.ByteArrayInputStream(data);
             java.io.DataInputStream dis = new java.io.DataInputStream(bis)) {
            dis.readByte();
            UUID playerId = new UUID(dis.readLong(), dis.readLong());
            String serverName = dis.readUTF();
            return new PlayerReadyPacket(playerId, serverName);
        } catch (IOException e) {
            throw new RuntimeException("Failed to deserialize PlayerReadyPacket", e);
        }
    }
}
