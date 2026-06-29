package pl.fejzu.persistence.paper.listener;

import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import pl.fejzu.persistence.player.PlayerDataLoader;
import pl.fejzu.persistence.result.LoadReason;
import pl.fejzu.persistence.result.SaveReason;
import pl.fejzu.persistence.settings.PlayerLifecycleSettings;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public final class PaperPlayerLifecycleListener implements Listener {

    private static final Logger LOGGER = Logger.getLogger(PaperPlayerLifecycleListener.class.getName());

    private final PlayerDataLoader playerDataLoader;
    private final PlayerLifecycleSettings settings;
    private final ConcurrentHashMap<UUID, CompletableFuture<Void>> pendingLoads = new ConcurrentHashMap<>();

    public PaperPlayerLifecycleListener(PlayerDataLoader playerDataLoader, PlayerLifecycleSettings settings) {
        this.playerDataLoader = playerDataLoader;
        this.settings = settings;
    }

    public PaperPlayerLifecycleListener(PlayerDataLoader playerDataLoader) {
        this(playerDataLoader, PlayerLifecycleSettings.defaults());
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onAsyncPreLogin(AsyncPlayerPreLoginEvent event) {
        if (event.getLoginResult() != AsyncPlayerPreLoginEvent.Result.ALLOWED) return;
        if (!settings.isLoadOnJoin()) return;

        UUID playerId = event.getUniqueId();
        String playerName = event.getName();

        CompletableFuture<Void> loadFuture = playerDataLoader.loadPlayer(playerId, playerName, LoadReason.PLAYER_JOIN);
        pendingLoads.put(playerId, loadFuture);

        try {
            loadFuture.get(settings.getLoadTimeoutSeconds(), TimeUnit.SECONDS);
        } catch (java.util.concurrent.TimeoutException e) {
            LOGGER.warning("Timed out loading player data for " + playerName
                + " after " + settings.getLoadTimeoutSeconds() + "s");
            if (settings.isKickOnLoadFailure()) {
                event.disallow(
                    AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                    Component.text(settings.getKickMessage())
                );
            }
        } catch (Exception e) {
            LOGGER.warning("Failed to load player data for " + playerName + ": " + e.getMessage());
            if (settings.isKickOnLoadFailure()) {
                event.disallow(
                    AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                    Component.text(settings.getKickMessage())
                );
            }
        } finally {
            pendingLoads.remove(playerId);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        UUID playerId = event.getPlayer().getUniqueId();
        CompletableFuture<Void> pending = pendingLoads.remove(playerId);
        if (pending != null) {
            pending.cancel(false);
        }
        if (!settings.isSaveOnQuit() && !settings.isUnloadOnQuit()) return;

        playerDataLoader.unloadPlayer(playerId, SaveReason.PLAYER_QUIT)
            .exceptionally(throwable -> {
                LOGGER.warning("Failed to save player data for "
                    + event.getPlayer().getName() + ": " + throwable.getMessage());
                return null;
            });
    }
}
