package pl.fejzu.persistence.nats;

import io.nats.client.Connection;
import io.nats.client.Nats;
import io.nats.client.Options;
import pl.fejzu.persistence.nats.config.NatsConfiguration;

import java.io.IOException;
import java.time.Duration;

public final class NatsConnectionFactory {

    private final NatsConfiguration configuration;

    public NatsConnectionFactory(NatsConfiguration configuration) {
        this.configuration = configuration;
    }

    public Connection createConnection() throws IOException, InterruptedException {
        Options.Builder optionsBuilder = new Options.Builder()
            .server(configuration.getUrl())
            .connectionTimeout(Duration.ofMillis(configuration.getConnectionTimeoutMs()))
            .maxReconnects(configuration.getMaxReconnects())
            .reconnectWait(Duration.ofMillis(configuration.getReconnectWaitMs()));

        if (configuration.getToken() != null && !configuration.getToken().isBlank()) {
            optionsBuilder.token(configuration.getToken().toCharArray());
        } else if (configuration.getUsername() != null && !configuration.getUsername().isBlank()) {
            optionsBuilder.userInfo(
                configuration.getUsername().toCharArray(),
                configuration.getPassword() != null ? configuration.getPassword().toCharArray() : new char[0]
            );
        }

        return Nats.connect(optionsBuilder.build());
    }
}
