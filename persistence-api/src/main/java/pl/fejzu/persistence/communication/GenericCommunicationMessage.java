package pl.fejzu.persistence.communication;

import java.util.Collections;
import java.util.Map;

public final class GenericCommunicationMessage implements CommunicationMessage {

    private final String type;
    private final byte[] payload;
    private final Map<String, String> headers;

    private GenericCommunicationMessage(String type, byte[] payload, Map<String, String> headers) {
        this.type = type;
        this.payload = payload;
        this.headers = headers;
    }

    public static GenericCommunicationMessage of(String type, byte[] payload) {
        return new GenericCommunicationMessage(type, payload, Collections.emptyMap());
    }

    public static GenericCommunicationMessage of(String type, byte[] payload, Map<String, String> headers) {
        return new GenericCommunicationMessage(type, payload, Collections.unmodifiableMap(headers));
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public byte[] getPayload() {
        return payload;
    }

    @Override
    public Map<String, String> getHeaders() {
        return headers;
    }

    @Override
    public String getHeader(String key) {
        return headers.get(key);
    }
}
