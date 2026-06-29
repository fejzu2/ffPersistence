package pl.fejzu.persistence.nats;

import lombok.RequiredArgsConstructor;
import pl.fejzu.persistence.communication.CommunicationMessage;

import java.util.Collections;
import java.util.Map;

@RequiredArgsConstructor
public final class NatsMessage implements CommunicationMessage {

    private final String type;
    private final byte[] payload;

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
        return Collections.emptyMap();
    }

    @Override
    public String getHeader(String key) {
        return null;
    }
}
