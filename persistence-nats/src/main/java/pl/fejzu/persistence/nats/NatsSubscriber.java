package pl.fejzu.persistence.nats;

import io.nats.client.Connection;
import io.nats.client.Dispatcher;
import pl.fejzu.persistence.nats.codec.NatsMessageCodec;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public final class NatsSubscriber {

    private final Connection connection;
    private final NatsMessageCodec codec = new NatsMessageCodec();
    private final Map<String, Dispatcher> dispatchers = new ConcurrentHashMap<>();

    public NatsSubscriber(Connection connection) {
        this.connection = connection;
    }

    public void subscribe(String subject, Consumer<NatsMessage> handler) {
        Dispatcher dispatcher = connection.createDispatcher(msg -> {
            NatsMessage message = codec.decode(msg.getData());
            handler.accept(message);
        });
        dispatcher.subscribe(subject);
        dispatchers.put(subject, dispatcher);
    }

    public void unsubscribe(String subject) {
        Dispatcher dispatcher = dispatchers.remove(subject);
        if (dispatcher != null) {
            dispatcher.unsubscribe(subject);
        }
    }

    public void unsubscribeAll() {
        dispatchers.forEach((subject, dispatcher) -> dispatcher.unsubscribe(subject));
        dispatchers.clear();
    }
}
