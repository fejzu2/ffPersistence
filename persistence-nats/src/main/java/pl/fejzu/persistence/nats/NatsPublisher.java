package pl.fejzu.persistence.nats;

import io.nats.client.Connection;
import pl.fejzu.persistence.communication.CommunicationMessage;
import pl.fejzu.persistence.nats.codec.NatsMessageCodec;

public final class NatsPublisher {

    private final Connection connection;
    private final NatsMessageCodec codec = new NatsMessageCodec();

    public NatsPublisher(Connection connection) {
        this.connection = connection;
    }

    public void publish(String subject, CommunicationMessage message) {
        byte[] data = codec.encode(message);
        connection.publish(subject, data);
    }

    public void publishRaw(String subject, byte[] data) {
        connection.publish(subject, data);
    }
}
