package pl.fejzu.persistence.messaging.packet;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public final class PlayerTransferStartPacket implements PluginMessagingPacket {

    private final UUID playerId;
    private final String playerName;
    private final String sourceSector;
    private final String targetSector;
    private final String targetServer;

    @Override
    public PluginMessagingPacketType getType() {
        return PluginMessagingPacketType.PLAYER_TRANSFER_START;
    }

    @Override
    public byte[] serialize() {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             DataOutputStream dos = new DataOutputStream(bos)) {
            dos.writeByte(getType().getId());
            dos.writeLong(playerId.getMostSignificantBits());
            dos.writeLong(playerId.getLeastSignificantBits());
            dos.writeUTF(playerName);
            dos.writeUTF(sourceSector);
            dos.writeUTF(targetSector);
            dos.writeUTF(targetServer);
            return bos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Failed to serialize PlayerTransferStartPacket", e);
        }
    }

    public static PlayerTransferStartPacket deserialize(byte[] data) {
        try (DataInputStream dis = new DataInputStream(new ByteArrayInputStream(data))) {
            dis.readByte();
            UUID playerId = new UUID(dis.readLong(), dis.readLong());
            String playerName = dis.readUTF();
            String sourceSector = dis.readUTF();
            String targetSector = dis.readUTF();
            String targetServer = dis.readUTF();
            return new PlayerTransferStartPacket(playerId, playerName, sourceSector, targetSector, targetServer);
        } catch (IOException e) {
            throw new RuntimeException("Failed to deserialize PlayerTransferStartPacket", e);
        }
    }
}
