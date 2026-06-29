package pl.fejzu.persistence.paper.display;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import pl.fejzu.persistence.display.LoadingAnimation;
import pl.fejzu.persistence.settings.BossBarColor;
import pl.fejzu.persistence.settings.BossBarStyle;
import pl.fejzu.persistence.settings.LoadingSettings;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class PaperBossBarLoadingDisplay implements PaperLoadingDisplay {

    private final Server server;
    private final BossBar.Color color;
    private final BossBar.Overlay overlay;
    private final Map<UUID, BossBar> bossBars = new ConcurrentHashMap<>();

    public PaperBossBarLoadingDisplay(Server server) {
        this(server, BossBar.Color.BLUE, BossBar.Overlay.PROGRESS);
    }

    public PaperBossBarLoadingDisplay(Server server, LoadingSettings settings) {
        this(server, toAdventureColor(settings.getBossBarColor()), toAdventureOverlay(settings.getBossBarStyle()));
    }

    public PaperBossBarLoadingDisplay(Server server, BossBar.Color color, BossBar.Overlay overlay) {
        this.server = server;
        this.color = color;
        this.overlay = overlay;
    }

    @Override
    public void show(UUID playerId, String title, LoadingAnimation animation) {
        Player player = server.getPlayer(playerId);
        if (player == null || !player.isOnline()) return;

        BossBar bar = BossBar.bossBar(Component.text(title), 1.0f, color, overlay);
        bossBars.put(playerId, bar);
        player.showBossBar(bar);
    }

    @Override
    public void update(UUID playerId, String text) {
        BossBar bar = bossBars.get(playerId);
        if (bar == null) return;
        bar.name(Component.text(text));
    }

    @Override
    public void hide(UUID playerId) {
        BossBar bar = bossBars.remove(playerId);
        if (bar == null) return;
        Player player = server.getPlayer(playerId);
        if (player != null && player.isOnline()) {
            player.hideBossBar(bar);
        }
    }

    private static BossBar.Color toAdventureColor(BossBarColor color) {
        return switch (color) {
            case PINK -> BossBar.Color.PINK;
            case BLUE -> BossBar.Color.BLUE;
            case RED -> BossBar.Color.RED;
            case GREEN -> BossBar.Color.GREEN;
            case YELLOW -> BossBar.Color.YELLOW;
            case PURPLE -> BossBar.Color.PURPLE;
            case WHITE -> BossBar.Color.WHITE;
        };
    }

    private static BossBar.Overlay toAdventureOverlay(BossBarStyle style) {
        return switch (style) {
            case PROGRESS -> BossBar.Overlay.PROGRESS;
            case NOTCHED_6 -> BossBar.Overlay.NOTCHED_6;
            case NOTCHED_10 -> BossBar.Overlay.NOTCHED_10;
            case NOTCHED_12 -> BossBar.Overlay.NOTCHED_12;
            case NOTCHED_20 -> BossBar.Overlay.NOTCHED_20;
        };
    }
}
