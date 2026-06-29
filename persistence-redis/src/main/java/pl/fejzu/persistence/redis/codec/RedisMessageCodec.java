package pl.fejzu.persistence.redis.codec;

import pl.fejzu.persistence.communication.CommunicationMessage;
import pl.fejzu.persistence.redis.RedisMessage;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public final class RedisMessageCodec {

    public String encode(CommunicationMessage message) {
        String payloadBase64 = Base64.getEncoder().encodeToString(message.getPayload());
        return message.getType() + ":" + payloadBase64;
    }

    public RedisMessage decode(String raw) {
        int colonIndex = raw.indexOf(':');
        if (colonIndex < 0) {
            throw new IllegalArgumentException("Invalid Redis message format: " + raw);
        }
        String type = raw.substring(0, colonIndex);
        byte[] payload = Base64.getDecoder().decode(raw.substring(colonIndex + 1));
        return new RedisMessage(type, payload);
    }
}
