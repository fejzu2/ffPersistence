package pl.fejzu.persistence.communication;

import java.util.function.Consumer;

public interface CommunicationBus {

    void connect();

    void disconnect();

    void publish(CommunicationChannel channel, CommunicationMessage message);

    void subscribe(CommunicationChannel channel, Consumer<CommunicationMessage> handler);

    void unsubscribe(CommunicationChannel channel);

    boolean isConnected();

    String getBusName();
}
