package pl.fejzu.persistence.paper.platform;

import org.bukkit.plugin.java.JavaPlugin;
import pl.fejzu.persistence.proxy.ServerPlatform;

public final class PaperPlatformAdapter {

    private final JavaPlugin plugin;

    public PaperPlatformAdapter(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public ServerPlatform detectPlatform() {
        try {
            Class.forName("io.papermc.paper.configuration.Configuration");
            return ServerPlatform.PAPER;
        } catch (ClassNotFoundException ignored) {
            return ServerPlatform.SPIGOT;
        }
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }

    public String getServerVersion() {
        return plugin.getServer().getVersion();
    }
}
