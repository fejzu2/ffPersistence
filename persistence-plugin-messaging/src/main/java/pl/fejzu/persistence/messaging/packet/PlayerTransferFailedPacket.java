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
public final class PlayerTransferFailedPacket implements PluginMessagingPacket {

    private final UUID playerId;
    private final String targetSector;
    private final String reason;

    @Override
    public PluginMessagingPacketType getType() {
        return PluginMessagingPacketType.PLAYER_TRANSFER_FAILED;
    }

    @Override
    public byte[] serialize() {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             DataOutputStream dos = new DataOutputStream(bos)) {
            dos.writeByte(getType().getId());
            dos.writeLong(playerId.getMostSignificantBits());
            dos.writeLong(playerId.getLeastSignificantBits());
            dos.writeUTF(targetSector);
            dos.writeUTF(reason);
            return bos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Failed to serialize PlayerTransferFailedPacket", e);
        }
    }

    public static PlayerTransferFailedPacket deserialize(byte[] data) {
        try (DataInputStream dis = new DataInputStream(new ByteArrayInputStream(data))) {
            dis.readByte();
            UUID playerId = new UUID(dis.readLong(), dis.readLong());
            String targetSector = dis.readUTF();
            String reason = dis.readUTF();
            return new PlayerTransferFailedPacket(playerId, targetSector, reason);
        } catch (IOException e) {
            throw new RuntimeException("Failed to deserialize PlayerTransferFailedPacket", e);
        }
    }
}
