package pl.fejzu.persistence.paper.display;

import net.kyori.adventure.text.Component;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import pl.fejzu.persistence.display.LoadingAnimation;

import java.util.UUID;

public final class PaperActionBarLoadingDisplay implements PaperLoadingDisplay {

    private final Server server;

    public PaperActionBarLoadingDisplay(Server server) {
        this.server = server;
    }

    @Override
    public void show(UUID playerId, String title, LoadingAnimation animation) {
        Player player = server.getPlayer(playerId);
        if (player == null || !player.isOnline()) return;
        player.sendActionBar(Component.text(title));
    }

    @Override
    public void update(UUID playerId, String text) {
        Player player = server.getPlayer(playerId);
        if (player == null || !player.isOnline()) return;
        player.sendActionBar(Component.text(text));
    }

    @Override
    public void hide(UUID playerId) {
        Player player = server.getPlayer(playerId);
        if (player == null || !player.isOnline()) return;
        player.sendActionBar(Component.empty());
    }
}
