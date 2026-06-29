package pl.fejzu.persistence.paper.display;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import pl.fejzu.persistence.display.LoadingAnimation;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public final class PaperLoadingAnimationTask {

    private final JavaPlugin plugin;
    private final PaperLoadingDisplay display;
    private final LoadingAnimation animation;
    private final String baseText;
    private final long periodTicks;

    private BukkitTask task;
    private final AtomicInteger tick = new AtomicInteger(0);
    private final Map<UUID, Boolean> activePlayers = new ConcurrentHashMap<>();

    public PaperLoadingAnimationTask(
        JavaPlugin plugin,
        PaperLoadingDisplay display,
        LoadingAnimation animation,
        String baseText,
        long animationIntervalMs
    ) {
        this.plugin = plugin;
        this.display = display;
        this.animation = animation;
        this.baseText = baseText;
        this.periodTicks = Math.max(1L, animationIntervalMs / 50);
    }

    public PaperLoadingAnimationTask(
        JavaPlugin plugin,
        PaperLoadingDisplay display,
        LoadingAnimation animation,
        String baseText
    ) {
        this(plugin, display, animation, baseText, 100);
    }

    public void startFor(UUID playerId) {
        activePlayers.put(playerId, true);
        ensureRunning();
    }

    public void stopFor(UUID playerId) {
        activePlayers.remove(playerId);
        display.hide(playerId);
        if (activePlayers.isEmpty()) {
            stop();
        }
    }

    private void ensureRunning() {
        if (task != null && !task.isCancelled()) return;
        task = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            int currentTick = tick.getAndIncrement();
            String frame = animation.getFrame(currentTick);
            String text = frame + " " + baseText + " " + frame;
            activePlayers.keySet().forEach(playerId -> display.update(playerId, text));
        }, 0L, periodTicks);
    }

    private void stop() {
        if (task != null) {
            task.cancel();
            task = null;
        }
        tick.set(0);
    }
}
