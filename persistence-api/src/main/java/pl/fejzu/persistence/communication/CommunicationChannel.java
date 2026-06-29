package pl.fejzu.persistence.communication;

import lombok.Value;

@Value
public class CommunicationChannel {

    String name;

    public static CommunicationChannel of(String name) {
        return new CommunicationChannel(name);
    }
}
