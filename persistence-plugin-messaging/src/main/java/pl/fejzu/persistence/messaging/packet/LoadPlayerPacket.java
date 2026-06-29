package pl.fejzu.persistence.messaging.packet;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public final class LoadPlayerPacket implements PluginMessagingPacket {

    private final UUID playerId;
    private final String playerName;
    private final String targetServer;

    @Override
    public PluginMessagingPacketType getType() {
        return PluginMessagingPacketType.LOAD_PLAYER;
    }

    @Override
    public byte[] serialize() {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             DataOutputStream dos = new DataOutputStream(bos)) {
            dos.writeByte(getType().getId());
            dos.writeLong(playerId.getMostSignificantBits());
            dos.writeLong(playerId.getLeastSignificantBits());
            dos.writeUTF(playerName);
            dos.writeUTF(targetServer);
            return bos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Failed to serialize LoadPlayerPacket", e);
        }
    }

    public static LoadPlayerPacket deserialize(byte[] data) {
        try (java.io.ByteArrayInputStream bis = new java.io.ByteArrayInputStream(data);
             java.io.DataInputStream dis = new java.io.DataInputStream(bis)) {
            dis.readByte();
            UUID playerId = new UUID(dis.readLong(), dis.readLong());
            String playerName = dis.readUTF();
            String targetServer = dis.readUTF();
            return new LoadPlayerPacket(playerId, playerName, targetServer);
        } catch (IOException e) {
            throw new RuntimeException("Failed to deserialize LoadPlayerPacket", e);
        }
    }
}
