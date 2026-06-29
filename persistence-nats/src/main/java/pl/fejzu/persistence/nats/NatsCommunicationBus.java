package pl.fejzu.persistence.nats;

import io.nats.client.Connection;
import pl.fejzu.persistence.communication.CommunicationBus;
import pl.fejzu.persistence.communication.CommunicationChannel;
import pl.fejzu.persistence.communication.CommunicationMessage;
import pl.fejzu.persistence.nats.config.NatsConfiguration;

import java.util.function.Consumer;
import java.util.logging.Logger;

public final class NatsCommunicationBus implements CommunicationBus {

    private static final Logger LOGGER = Logger.getLogger(NatsCommunicationBus.class.getName());

    private final NatsConfiguration configuration;
    private final NatsConnectionFactory connectionFactory;

    private Connection connection;
    private NatsPublisher publisher;
    private NatsSubscriber subscriber;
    private volatile boolean connected = false;

    public NatsCommunicationBus(NatsConfiguration configuration) {
        this.configuration = configuration;
        this.connectionFactory = new NatsConnectionFactory(configuration);
    }

    @Override
    public void connect() {
        if (connected) return;
        try {
            LOGGER.info("Connecting to NATS: " + configuration.getUrl());
            connection = connectionFactory.createConnection();
            publisher = new NatsPublisher(connection);
            subscriber = new NatsSubscriber(connection);
            connected = true;
            LOGGER.info("Connected to NATS");
        } catch (Exception e) {
            throw new RuntimeException("Failed to connect to NATS: " + configuration.getUrl(), e);
        }
    }

    @Override
    public void disconnect() {
        if (!connected) return;
        try {
            if (subscriber != null) {
                subscriber.unsubscribeAll();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (Exception e) {
            LOGGER.warning("Error during NATS disconnect: " + e.getMessage());
        } finally {
            connected = false;
            LOGGER.info("Disconnected from NATS");
        }
    }

    @Override
    public void publish(CommunicationChannel channel, CommunicationMessage message) {
        if (!connected) throw new IllegalStateException("NATS bus is not connected");
        publisher.publish(channel.getName(), message);
    }

    @Override
    public void subscribe(CommunicationChannel channel, Consumer<CommunicationMessage> handler) {
        if (!connected) throw new IllegalStateException("NATS bus is not connected");
        subscriber.subscribe(channel.getName(), handler::accept);
    }

    @Override
    public void unsubscribe(CommunicationChannel channel) {
        if (subscriber != null) {
            subscriber.unsubscribe(channel.getName());
        }
    }

    @Override
    public boolean isConnected() {
        return connected && connection != null && connection.getStatus() == Connection.Status.CONNECTED;
    }

    @Override
    public String getBusName() {
        return "NATS";
    }
}
