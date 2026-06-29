package pl.fejzu.persistence.examples.bungee.command;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;
import pl.fejzu.persistence.examples.common.sector.ExampleLastSectorProvider;
import pl.fejzu.persistence.sector.SectorId;
import pl.fejzu.persistence.sector.SectorService;
import pl.fejzu.persistence.sync.SyncService;

import java.util.List;
import java.util.UUID;

public final class ExampleBungeeCommands {

    private ExampleBungeeCommands() {}

    public static List<Command> create(
        Plugin plugin,
        SyncService syncService,
        SectorService sectorService,
        ExampleLastSectorProvider lastSectorProvider
    ) {
        ProxyServer proxy = plugin.getProxy();
        return List.of(
            new SendSectorCommand(proxy, sectorService),
            new ConnectLastCommand(proxy, sectorService, lastSectorProvider)
        );
    }

    private static final class SendSectorCommand extends Command {

        private final ProxyServer proxy;
        private final SectorService sectorService;

        SendSectorCommand(ProxyServer proxy, SectorService sectorService) {
            super("sendsector");
            this.proxy = proxy;
            this.sectorService = sectorService;
        }

        @Override
        public void execute(CommandSender sender, String[] args) {
            if (args.length < 2) {
                sender.sendMessage(new TextComponent("Usage: /sendsector <player> <sector>"));
                return;
            }
            ProxiedPlayer target = proxy.getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(new TextComponent("Player not found: " + args[0]));
                return;
            }
            String sector = args[1];
            sectorService.transfer(target.getUniqueId())
                .to(SectorId.of(sector))
                .syncFullPlayer(true)
                .preload(true)
                .lockData(true)
                .execute()
                .thenAccept(result -> sender.sendMessage(new TextComponent(
                    result.isSuccess()
                        ? "Transferred " + args[0] + " to sector " + sector
                        : "Transfer failed: " + result.getErrorMessage()
                )));
        }
    }

    private static final class ConnectLastCommand extends Command {

        private final ProxyServer proxy;
        private final SectorService sectorService;
        private final ExampleLastSectorProvider lastSectorProvider;

        ConnectLastCommand(ProxyServer proxy, SectorService sectorService, ExampleLastSectorProvider lastSectorProvider) {
            super("connectlast");
            this.proxy = proxy;
            this.sectorService = sectorService;
            this.lastSectorProvider = lastSectorProvider;
        }

        @Override
        public void execute(CommandSender sender, String[] args) {
            if (args.length < 1) {
                sender.sendMessage(new TextComponent("Usage: /connectlast <player>"));
                return;
            }
            ProxiedPlayer target = proxy.getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(new TextComponent("Player not found: " + args[0]));
                return;
            }
            UUID playerId = target.getUniqueId();
            lastSectorProvider.getLastSector(playerId).thenAccept(lastSectorOpt -> {
                if (lastSectorOpt.isEmpty()) {
                    sender.sendMessage(new TextComponent("No last sector for " + args[0]));
                    return;
                }
                SectorId lastSector = lastSectorOpt.get();
                sectorService.getSector(lastSector).ifPresentOrElse(
                    registeredSector -> {
                        SectorId target2 = registeredSector.isOnline()
                            ? lastSector
                            : sectorService.getSector(SectorId.of("lobby"))
                                .map(s -> s.getId())
                                .orElse(lastSector);
                        sectorService.transfer(playerId)
                            .to(target2)
                            .syncFullPlayer(true)
                            .preload(true)
                            .execute()
                            .thenAccept(result -> sender.sendMessage(new TextComponent(
                                result.isSuccess()
                                    ? "Reconnected " + args[0] + " to " + target2
                                    : "Reconnect failed: " + result.getErrorMessage()
                            )));
                    },
                    () -> sender.sendMessage(new TextComponent("Sector not registered: " + lastSector))
                );
            });
        }
    }
}
