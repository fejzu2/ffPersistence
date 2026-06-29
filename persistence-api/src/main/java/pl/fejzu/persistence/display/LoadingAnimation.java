package pl.fejzu.persistence.display;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LoadingAnimation {

    DOTS(new String[]{"⠋", "⠙", "⠹", "⠸", "⠼", "⠴", "⠦", "⠧", "⠇", "⠏"}),
    SPINNER(new String[]{"◜", "◝", "◞", "◟"}),
    ARROWS(new String[]{"←", "↖", "↑", "↗", "→", "↘", "↓", "↙"}),
    BOUNCING(new String[]{"▁", "▃", "▄", "▅", "▆", "▇", "▆", "▅", "▄", "▃"});

    private final String[] frames;

    public String getFrame(int tick) {
        return frames[tick % frames.length];
    }
}
