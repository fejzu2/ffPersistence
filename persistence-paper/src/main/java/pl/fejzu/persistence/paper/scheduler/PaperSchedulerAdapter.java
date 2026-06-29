package pl.fejzu.persistence.paper.scheduler;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public final class PaperSchedulerAdapter {

    private final JavaPlugin plugin;

    public PaperSchedulerAdapter(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public BukkitTask runAsync(Runnable runnable) {
        return plugin.getServer().getScheduler().runTaskAsynchronously(plugin, runnable);
    }

    public BukkitTask runSync(Runnable runnable) {
        return plugin.getServer().getScheduler().runTask(plugin, runnable);
    }

    public BukkitTask runAsyncRepeating(Runnable runnable, long delayTicks, long periodTicks) {
        return plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, runnable, delayTicks, periodTicks);
    }

    public BukkitTask runSyncRepeating(Runnable runnable, long delayTicks, long periodTicks) {
        return plugin.getServer().getScheduler().runTaskTimer(plugin, runnable, delayTicks, periodTicks);
    }

    public BukkitTask runSyncLater(Runnable runnable, long delayTicks) {
        return plugin.getServer().getScheduler().runTaskLater(plugin, runnable, delayTicks);
    }
}
