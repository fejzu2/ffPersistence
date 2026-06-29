package pl.fejzu.persistence.display;

import java.util.UUID;

public interface LoadingDisplay {

    void show(UUID playerId, String title, LoadingAnimation animation);

    void update(UUID playerId, String text);

    void hide(UUID playerId);
}
