package pl.fejzu.persistence.settings;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import pl.fejzu.persistence.display.LoadingAnimation;

@Builder(toBuilder = true)
@Getter
@EqualsAndHashCode
public final class LoadingSettings {

    @Builder.Default
    private final boolean enabled = true;

    @Builder.Default
    private final String title = "Loading...";

    @Builder.Default
    private final String subtitle = "Please wait";

    @Builder.Default
    private final LoadingAnimation animation = LoadingAnimation.DOTS;

    @Builder.Default
    private final LoadingDisplayType displayType = LoadingDisplayType.BOSS_BAR;

    @Builder.Default
    private final BossBarColor bossBarColor = BossBarColor.BLUE;

    @Builder.Default
    private final BossBarStyle bossBarStyle = BossBarStyle.PROGRESS;

    @Builder.Default
    private final long animationIntervalMs = 100;

    @Builder.Default
    private final long loadTimeoutSeconds = 10;

    @Builder.Default
    private final boolean hideOnSuccess = true;

    @Builder.Default
    private final LoadingMessages messages = LoadingMessages.defaults();

    public static LoadingSettings defaults() {
        return builder().build();
    }

    public static LoadingSettings disabled() {
        return builder().enabled(false).build();
    }

    public static LoadingSettings actionBar() {
        return builder().displayType(LoadingDisplayType.ACTION_BAR).build();
    }
}
