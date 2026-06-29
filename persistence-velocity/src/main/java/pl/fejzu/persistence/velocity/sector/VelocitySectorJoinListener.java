package pl.fejzu.persistence.velocity.sector;

import com.velocitypowered.api.event.EventTask;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerChooseInitialServerEvent;
import com.velocitypowered.api.proxy.ProxyServer;
import pl.fejzu.persistence.sector.SectorConnectResult;

import java.util.logging.Logger;

public final class VelocitySectorJoinListener {

    private static final Logger LOGGER = Logger.getLogger(VelocitySectorJoinListener.class.getName());

    private final ProxyServer proxy;
    private final VelocitySectorConnectService connectService;

    public VelocitySectorJoinListener(ProxyServer proxy, VelocitySectorConnectService connectService) {
        this.proxy = proxy;
        this.connectService = connectService;
    }

    @Subscribe(order = PostOrder.EARLY)
    public EventTask onChooseInitialServer(PlayerChooseInitialServerEvent event) {
        return EventTask.async(() -> {
            SectorConnectResult result = connectService.connectOnJoin(
                event.getPlayer().getUniqueId(),
                event.getPlayer().getUsername()
            ).join();

            if (result.isSuccess() && result.getServerName() != null) {
                proxy.getServer(result.getServerName()).ifPresent(event::setInitialServer);
            } else {
                LOGGER.warning("Sector join failed for " + event.getPlayer().getUsername() + ": " + result.getErrorMessage());
            }
        });
    }
}
