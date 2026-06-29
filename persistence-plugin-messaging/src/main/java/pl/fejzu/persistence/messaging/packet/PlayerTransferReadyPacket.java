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
public final class PlayerTransferReadyPacket implements PluginMessagingPacket {

    private final UUID playerId;
    private final String targetSector;
    private final String serverName;

    @Override
    public PluginMessagingPacketType getType() {
        return PluginMessagingPacketType.PLAYER_TRANSFER_READY;
    }

    @Override
    public byte[] serialize() {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             DataOutputStream dos = new DataOutputStream(bos)) {
            dos.writeByte(getType().getId());
            dos.writeLong(playerId.getMostSignificantBits());
            dos.writeLong(playerId.getLeastSignificantBits());
            dos.writeUTF(targetSector);
            dos.writeUTF(serverName);
            return bos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Failed to serialize PlayerTransferReadyPacket", e);
        }
    }

    public static PlayerTransferReadyPacket deserialize(byte[] data) {
        try (DataInputStream dis = new DataInputStream(new ByteArrayInputStream(data))) {
            dis.readByte();
            UUID playerId = new UUID(dis.readLong(), dis.readLong());
            String targetSector = dis.readUTF();
            String serverName = dis.readUTF();
            return new PlayerTransferReadyPacket(playerId, targetSector, serverName);
        } catch (IOException e) {
            throw new RuntimeException("Failed to deserialize PlayerTransferReadyPacket", e);
        }
    }
}
