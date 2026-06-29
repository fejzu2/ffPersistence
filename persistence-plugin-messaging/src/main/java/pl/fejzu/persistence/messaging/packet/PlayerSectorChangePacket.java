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
public final class PlayerSectorChangePacket implements PluginMessagingPacket {

    private final UUID playerId;
    private final String sourceSector;
    private final String targetSector;

    @Override
    public PluginMessagingPacketType getType() {
        return PluginMessagingPacketType.PLAYER_SECTOR_CHANGE;
    }

    @Override
    public byte[] serialize() {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             DataOutputStream dos = new DataOutputStream(bos)) {
            dos.writeByte(getType().getId());
            dos.writeLong(playerId.getMostSignificantBits());
            dos.writeLong(playerId.getLeastSignificantBits());
            dos.writeUTF(sourceSector);
            dos.writeUTF(targetSector);
            return bos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Failed to serialize PlayerSectorChangePacket", e);
        }
    }

    public static PlayerSectorChangePacket deserialize(byte[] data) {
        try (DataInputStream dis = new DataInputStream(new ByteArrayInputStream(data))) {
            dis.readByte();
            UUID playerId = new UUID(dis.readLong(), dis.readLong());
            String sourceSector = dis.readUTF();
            String targetSector = dis.readUTF();
            return new PlayerSectorChangePacket(playerId, sourceSector, targetSector);
        } catch (IOException e) {
            throw new RuntimeException("Failed to deserialize PlayerSectorChangePacket", e);
        }
    }
}
