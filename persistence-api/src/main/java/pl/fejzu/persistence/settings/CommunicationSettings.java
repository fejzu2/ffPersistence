package pl.fejzu.persistence.settings;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Builder(toBuilder = true)
@Getter
@EqualsAndHashCode
public final class CommunicationSettings {

    @Builder.Default
    private final boolean enabled = false;

    @Builder.Default
    private final String defaultBus = "plugin-messaging";

    @Builder.Default
    private final long requestTimeoutMs = 5000;

    @Builder.Default
    private final String channelsPrefix = "ffpersistence";

    @Builder.Default
    private final boolean debugPackets = false;

    public static CommunicationSettings defaults() {
        return builder().build();
    }

    public static CommunicationSettings enabled() {
        return builder().enabled(true).build();
    }
}
