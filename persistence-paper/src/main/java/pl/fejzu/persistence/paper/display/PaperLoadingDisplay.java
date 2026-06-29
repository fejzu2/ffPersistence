package pl.fejzu.persistence.paper.display;

import pl.fejzu.persistence.display.LoadingAnimation;
import pl.fejzu.persistence.display.LoadingDisplay;

import java.util.UUID;

public interface PaperLoadingDisplay extends LoadingDisplay {

    void show(UUID playerId, String title, LoadingAnimation animation);

    void update(UUID playerId, String text);

    void hide(UUID playerId);
}
