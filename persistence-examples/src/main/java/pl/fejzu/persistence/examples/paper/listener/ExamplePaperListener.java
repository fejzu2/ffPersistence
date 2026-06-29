package pl.fejzu.persistence.examples.paper.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import pl.fejzu.persistence.examples.common.model.ExampleLocationData;
import pl.fejzu.persistence.examples.common.repository.ExampleUserRepository;
import pl.fejzu.persistence.examples.common.sector.ExampleLastSectorProvider;
import pl.fejzu.persistence.sector.SectorId;
import pl.fejzu.persistence.service.PersistenceService;
import pl.fejzu.persistence.sync.SyncService;

public final class ExamplePaperListener implements Listener {

    private final PersistenceService service;
    private final SyncService syncService;
    private final ExampleLastSectorProvider lastSectorProvider;

    public ExamplePaperListener(
        PersistenceService service,
        SyncService syncService,
        ExampleLastSectorProvider lastSectorProvider
    ) {
        this.service = service;
        this.syncService = syncService;
        this.lastSectorProvider = lastSectorProvider;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        ExampleUserRepository repo = service.repository(ExampleUserRepository.class);
        repo.getCached(event.getPlayer().getUniqueId()).ifPresentOrElse(
            user -> event.getPlayer().sendMessage("Welcome back, " + user.getName() + "! Coins: " + user.getCoins()),
            () -> event.getPlayer().sendMessage("Your data is still loading...")
        );
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        ExampleUserRepository repo = service.repository(ExampleUserRepository.class);
        repo.getCached(event.getPlayer().getUniqueId()).ifPresent(user -> {
            String currentSector = user.getLastSector();
            if (currentSector != null) {
                lastSectorProvider.saveLastSector(event.getPlayer().getUniqueId(), SectorId.of(currentSector));
            }
        });
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (!event.hasChangedBlock()) return;
        ExampleUserRepository repo = service.repository(ExampleUserRepository.class);
        repo.getCached(event.getPlayer().getUniqueId()).ifPresent(user -> {
            ExampleLocationData location = ExampleLocationData.builder()
                .world(event.getTo().getWorld().getName())
                .x(event.getTo().getX())
                .y(event.getTo().getY())
                .z(event.getTo().getZ())
                .yaw(event.getTo().getYaw())
                .pitch(event.getTo().getPitch())
                .build();
            user.setLastLocation(location);
            syncService.playerSync().syncField(event.getPlayer().getUniqueId(), "lastLocation");
        });
    }
}
