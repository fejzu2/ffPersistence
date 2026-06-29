package pl.fejzu.persistence.settings;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Builder(toBuilder = true)
@Getter
@EqualsAndHashCode
public final class QueueSettings {

    @Builder.Default
    private final boolean enabled = true;

    @Builder.Default
    private final boolean showPosition = true;

    @Builder.Default
    private final boolean preloadWhenFirst = true;

    @Builder.Default
    private final String loadingMessage = "Your data is being loaded, please wait...";

    public static QueueSettings defaults() {
        return builder().build();
    }

    public static QueueSettings silent() {
        return builder().showPosition(false).build();
    }
}
