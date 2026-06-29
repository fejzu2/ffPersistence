package pl.fejzu.persistence.messaging;

import lombok.Getter;
import pl.fejzu.persistence.communication.CommunicationMessage;

import java.util.Collections;
import java.util.Map;

@Getter
public final class PluginMessagingMessage implements CommunicationMessage {

    private final String type;
    private final byte[] payload;
    private final Map<String, String> headers;

    private PluginMessagingMessage(String type, byte[] payload, Map<String, String> headers) {
        this.type = type;
        this.payload = payload;
        this.headers = headers;
    }

    @Override
    public String getHeader(String key) {
        return headers.get(key);
    }

    public static PluginMessagingMessage of(String type, byte[] payload) {
        return new PluginMessagingMessage(type, payload, Collections.emptyMap());
    }

    public static PluginMessagingMessage of(String type, byte[] payload, Map<String, String> headers) {
        return new PluginMessagingMessage(type, payload, headers);
    }
}
