package pl.fejzu.persistence.settings;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Builder(toBuilder = true)
@Getter
@EqualsAndHashCode
public final class LoadingMessages {

    @Builder.Default
    private final String loading = "Loading your data...";

    @Builder.Default
    private final String success = "Your data has been loaded!";

    @Builder.Default
    private final String error = "Failed to load your data. Please try reconnecting.";

    @Builder.Default
    private final String timeout = "Data load timed out. Please try reconnecting.";

    @Builder.Default
    private final String kick = "Failed to load your player data. Please try again.";

    @Builder.Default
    private final String transferring = "Transferring you to the server...";

    public static LoadingMessages defaults() {
        return builder().build();
    }
}
