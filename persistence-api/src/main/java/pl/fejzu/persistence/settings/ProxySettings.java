package pl.fejzu.persistence.settings;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import pl.fejzu.persistence.proxy.ProxyPlatform;

@Builder(toBuilder = true)
@Getter
@EqualsAndHashCode
public final class ProxySettings {

    @Builder.Default
    private final boolean enabled = false;

    @Builder.Default
    private final ProxyPlatform platform = ProxyPlatform.NONE;

    @Builder.Default
    private final boolean waitForBackendReady = true;

    @Builder.Default
    private final long transferTimeoutSeconds = 15;

    @Builder.Default
    private final String fallbackServer = "lobby";

    public static ProxySettings defaults() {
        return builder().build();
    }

    public static ProxySettings velocity() {
        return builder()
            .enabled(true)
            .platform(ProxyPlatform.VELOCITY)
            .build();
    }

    public static ProxySettings bungeeCord() {
        return builder()
            .enabled(true)
            .platform(ProxyPlatform.BUNGEECORD)
            .build();
    }
}
