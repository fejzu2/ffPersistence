package pl.fejzu.persistence.communication;

import java.util.Map;

public interface CommunicationMessage {

    String getType();

    byte[] getPayload();

    Map<String, String> getHeaders();

    String getHeader(String key);
}
