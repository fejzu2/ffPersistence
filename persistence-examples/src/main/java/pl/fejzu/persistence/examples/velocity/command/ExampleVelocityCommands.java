package pl.fejzu.persistence.examples.velocity.command;

import com.velocitypowered.api.command.RawCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.Component;
import pl.fejzu.persistence.examples.common.sector.ExampleLastSectorProvider;
import pl.fejzu.persistence.sector.SectorId;
import pl.fejzu.persistence.sector.SectorService;
import pl.fejzu.persistence.sync.SyncService;
import java.util.Optional;
import java.util.UUID;

public final class ExampleVelocityCommands implements RawCommand {

    private final ProxyServer proxy;
    private final SyncService syncService;
    private final SectorService sectorService;
    private final ExampleLastSectorProvider lastSectorProvider;

    public ExampleVelocityCommands(
        ProxyServer proxy,
        SyncService syncService,
        SectorService sectorService,
        ExampleLastSectorProvider lastSectorProvider
    ) {
        this.proxy = proxy;
        this.syncService = syncService;
        this.sectorService = sectorService;
        this.lastSectorProvider = lastSectorProvider;
    }

    @Override
    public void execute(Invocation invocation) {
        String label = invocation.alias();
        String[] args = invocation.arguments().isBlank()
            ? new String[0]
            : invocation.arguments().split(" ");

        switch (label.toLowerCase()) {
            case "sendsector" -> handleSendSector(invocation, args);
            case "syncplayer" -> handleSyncPlayer(invocation, args);
            case "connectlast" -> handleConnectLast(invocation, args);
        }
    }

    private void handleSendSector(Invocation invocation, String[] args) {
        if (args.length < 2) {
            invocation.source().sendMessage(Component.text("Usage: /sendsector <player> <sector>"));
            return;
        }
        Optional<Player> target = proxy.getPlayer(args[0]);
        if (target.isEmpty()) {
            invocation.source().sendMessage(Component.text("Player not found: " + args[0]));
            return;
        }
        String sector = args[1];
        sectorService.transfer(target.get().getUniqueId())
            .to(SectorId.of(sector))
            .syncFullPlayer(true)
            .preload(true)
            .lockData(true)
            .execute()
            .thenAccept(result -> {
                if (result.isSuccess()) {
                    invocation.source().sendMessage(Component.text("Transferred " + args[0] + " to sector " + sector));
                } else {
                    invocation.source().sendMessage(Component.text("Transfer failed: " + result.getErrorMessage()));
                }
            });
    }

    private void handleSyncPlayer(Invocation invocation, String[] args) {
        if (args.length < 1) {
            invocation.source().sendMessage(Component.text("Usage: /syncplayer <player>"));
            return;
        }
        Optional<Player> target = proxy.getPlayer(args[0]);
        if (target.isEmpty()) {
            invocation.source().sendMessage(Component.text("Player not found: " + args[0]));
            return;
        }
        syncService.playerSync().syncFull(target.get().getUniqueId())
            .thenAccept(result -> invocation.source().sendMessage(
                Component.text("Sync for " + args[0] + ": " + result.getState())
            ));
    }

    private void handleConnectLast(Invocation invocation, String[] args) {
        if (args.length < 1) {
            invocation.source().sendMessage(Component.text("Usage: /connectlast <player>"));
            return;
        }
        Optional<Player> target = proxy.getPlayer(args[0]);
        if (target.isEmpty()) {
            invocation.source().sendMessage(Component.text("Player not found: " + args[0]));
            return;
        }
        UUID playerId = target.get().getUniqueId();
        lastSectorProvider.getLastSector(playerId).thenAccept(lastSectorOpt -> {
            if (lastSectorOpt.isEmpty()) {
                invocation.source().sendMessage(Component.text("No last sector for " + args[0]));
                return;
            }
            SectorId lastSector = lastSectorOpt.get();
            sectorService.getSector(lastSector).ifPresentOrElse(_ -> {
                sectorService.transfer(playerId)
                    .to(lastSector)
                    .syncFullPlayer(true)
                    .preload(true)
                    .execute()
                    .thenAccept(result -> {
                        if (result.isSuccess()) {
                            invocation.source().sendMessage(
                                Component.text("Reconnected " + args[0] + " to last sector: " + lastSector)
                            );
                        } else {
                            invocation.source().sendMessage(
                                Component.text("Reconnect failed: " + result.getErrorMessage())
                            );
                        }
                    });
            }, () -> invocation.source().sendMessage(
                Component.text("Last sector not registered: " + lastSector)
            ));
        });
    }
}
