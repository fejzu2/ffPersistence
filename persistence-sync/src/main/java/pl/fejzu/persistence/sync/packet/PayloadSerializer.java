package pl.fejzu.persistence.sync.packet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public final class PayloadSerializer {

    private static final byte TYPE_STRING = 0x01;
    private static final byte TYPE_LONG = 0x02;
    private static final byte TYPE_INT = 0x03;
    private static final byte TYPE_DOUBLE = 0x04;
    private static final byte TYPE_BOOLEAN = 0x05;
    private static final byte TYPE_UUID = 0x06;
    private static final byte TYPE_NULL = 0x00;

    public static byte[] serialize(Map<String, Object> payload) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             DataOutputStream dos = new DataOutputStream(bos)) {
            dos.writeInt(payload.size());
            for (Map.Entry<String, Object> entry : payload.entrySet()) {
                dos.writeUTF(entry.getKey());
                writeValue(dos, entry.getValue());
            }
            return bos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Failed to serialize payload", e);
        }
    }

    public static Map<String, Object> deserialize(byte[] data) {
        try (DataInputStream dis = new DataInputStream(new ByteArrayInputStream(data))) {
            int size = dis.readInt();
            Map<String, Object> result = new LinkedHashMap<>();
            for (int i = 0; i < size; i++) {
                String key = dis.readUTF();
                result.put(key, readValue(dis));
            }
            return result;
        } catch (IOException e) {
            throw new RuntimeException("Failed to deserialize payload", e);
        }
    }

    private static void writeValue(DataOutputStream dos, Object value) throws IOException {
        if (value == null) {
            dos.writeByte(TYPE_NULL);
        } else if (value instanceof String s) {
            dos.writeByte(TYPE_STRING);
            dos.writeUTF(s);
        } else if (value instanceof Long l) {
            dos.writeByte(TYPE_LONG);
            dos.writeLong(l);
        } else if (value instanceof Integer i) {
            dos.writeByte(TYPE_INT);
            dos.writeInt(i);
        } else if (value instanceof Double d) {
            dos.writeByte(TYPE_DOUBLE);
            dos.writeDouble(d);
        } else if (value instanceof Boolean b) {
            dos.writeByte(TYPE_BOOLEAN);
            dos.writeBoolean(b);
        } else if (value instanceof UUID u) {
            dos.writeByte(TYPE_UUID);
            dos.writeLong(u.getMostSignificantBits());
            dos.writeLong(u.getLeastSignificantBits());
        } else {
            dos.writeByte(TYPE_STRING);
            dos.writeUTF(String.valueOf(value));
        }
    }

    private static Object readValue(DataInputStream dis) throws IOException {
        byte type = dis.readByte();
        return switch (type) {
            case TYPE_NULL -> null;
            case TYPE_STRING -> dis.readUTF();
            case TYPE_LONG -> dis.readLong();
            case TYPE_INT -> dis.readInt();
            case TYPE_DOUBLE -> dis.readDouble();
            case TYPE_BOOLEAN -> dis.readBoolean();
            case TYPE_UUID -> new UUID(dis.readLong(), dis.readLong());
            default -> throw new IOException("Unknown value type: " + type);
        };
    }

    private PayloadSerializer() {
    }
}
