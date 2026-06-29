package pl.fejzu.persistence.nats.codec;

import pl.fejzu.persistence.communication.CommunicationMessage;
import pl.fejzu.persistence.nats.NatsMessage;

import java.util.Base64;

public final class NatsMessageCodec {

    public byte[] encode(CommunicationMessage message) {
        String payloadBase64 = Base64.getEncoder().encodeToString(message.getPayload());
        String encoded = message.getType() + ":" + payloadBase64;
        return encoded.getBytes(java.nio.charset.StandardCharsets.UTF_8);
    }

    public NatsMessage decode(byte[] data) {
        String raw = new String(data, java.nio.charset.StandardCharsets.UTF_8);
        int colonIndex = raw.indexOf(':');
        if (colonIndex < 0) {
            throw new IllegalArgumentException("Invalid NATS message format");
        }
        String type = raw.substring(0, colonIndex);
        byte[] payload = Base64.getDecoder().decode(raw.substring(colonIndex + 1));
        return new NatsMessage(type, payload);
    }
}
