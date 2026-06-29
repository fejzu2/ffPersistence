package pl.fejzu.persistence.bungee.sector;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import pl.fejzu.persistence.sector.SectorConnectResult;

import java.util.logging.Logger;

public final class BungeeSectorJoinListener implements Listener {

    private static final Logger LOGGER = Logger.getLogger(BungeeSectorJoinListener.class.getName());

    private final ProxyServer proxy;
    private final BungeeSectorConnectService connectService;

    public BungeeSectorJoinListener(ProxyServer proxy, BungeeSectorConnectService connectService) {
        this.proxy = proxy;
        this.connectService = connectService;
    }

    @EventHandler
    public void onPostLogin(PostLoginEvent event) {
        ProxiedPlayer player = event.getPlayer();
        connectService.connectOnJoin(player.getUniqueId(), player.getName())
            .thenAccept(result -> {
                if (result.isSuccess() && result.getServerName() != null) {
                    net.md_5.bungee.api.config.ServerInfo serverInfo = proxy.getServerInfo(result.getServerName());
                    if (serverInfo != null) {
                        player.connect(serverInfo);
                    }
                } else {
                    LOGGER.warning("Sector join failed for " + player.getName() + ": " + result.getErrorMessage());
                }
            });
    }
}
