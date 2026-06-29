package pl.fejzu.persistence.messaging.packet;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public final class ErrorPacket implements PluginMessagingPacket {

    private final UUID playerId;
    private final String errorMessage;

    @Override
    public PluginMessagingPacketType getType() {
        return PluginMessagingPacketType.ERROR;
    }

    @Override
    public byte[] serialize() {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             DataOutputStream dos = new DataOutputStream(bos)) {
            dos.writeByte(getType().getId());
            dos.writeLong(playerId.getMostSignificantBits());
            dos.writeLong(playerId.getLeastSignificantBits());
            dos.writeUTF(errorMessage);
            return bos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Failed to serialize ErrorPacket", e);
        }
    }

    public static ErrorPacket deserialize(byte[] data) {
        try (java.io.ByteArrayInputStream bis = new java.io.ByteArrayInputStream(data);
             java.io.DataInputStream dis = new java.io.DataInputStream(bis)) {
            dis.readByte();
            UUID playerId = new UUID(dis.readLong(), dis.readLong());
            String errorMessage = dis.readUTF();
            return new ErrorPacket(playerId, errorMessage);
        } catch (IOException e) {
            throw new RuntimeException("Failed to deserialize ErrorPacket", e);
        }
    }
}
