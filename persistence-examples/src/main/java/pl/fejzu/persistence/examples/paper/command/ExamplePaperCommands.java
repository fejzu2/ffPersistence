package pl.fejzu.persistence.examples.paper.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.fejzu.persistence.examples.common.repository.ExampleUserRepository;
import pl.fejzu.persistence.result.LoadReason;
import pl.fejzu.persistence.result.SaveReason;
import pl.fejzu.persistence.sector.SectorId;
import pl.fejzu.persistence.sector.SectorService;
import pl.fejzu.persistence.service.PersistenceService;
import pl.fejzu.persistence.sync.SyncService;

import java.util.Optional;

public final class ExamplePaperCommands implements CommandExecutor {

    private final PersistenceService service;
    private final SyncService syncService;
    private final SectorService sectorService;

    public ExamplePaperCommands(PersistenceService service, SyncService syncService, SectorService sectorService) {
        this.service = service;
        this.syncService = syncService;
        this.sectorService = sectorService;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }
        return switch (command.getName().toLowerCase()) {
            case "coins" -> handleCoins(player);
            case "addcoins" -> handleAddCoins(player, args);
            case "sector" -> handleSector(player, args);
            case "sync" -> handleSync(player);
            case "save" -> handleSave(player);
            case "load" -> handleLoad(player);
            default -> false;
        };
    }

    private boolean handleCoins(Player player) {
        ExampleUserRepository repo = service.repository(ExampleUserRepository.class);
        Optional<pl.fejzu.persistence.examples.common.model.ExampleUser> user = repo.getCached(player.getUniqueId());
        if (user.isEmpty()) {
            player.sendMessage("Your data is not loaded yet.");
            return true;
        }
        player.sendMessage("Coins: " + user.get().getCoins());
        return true;
    }

    private boolean handleAddCoins(Player player, String[] args) {
        if (args.length < 1) {
            player.sendMessage("Usage: /addcoins <amount>");
            return true;
        }
        long amount;
        try {
            amount = Long.parseLong(args[0]);
        } catch (NumberFormatException e) {
            player.sendMessage("Invalid amount.");
            return true;
        }
        ExampleUserRepository repo = service.repository(ExampleUserRepository.class);
        repo.addCoins(player.getUniqueId(), amount).thenRun(() -> {
            syncService.playerSync().syncField(player.getUniqueId(), "coins");
            player.sendMessage("Added " + amount + " coins.");
        });
        return true;
    }

    private boolean handleSector(Player player, String[] args) {
        if (args.length < 1) {
            player.sendMessage("Usage: /sector <sector>");
            return true;
        }
        String targetSector = args[0];
        sectorService.transfer(player.getUniqueId())
            .to(SectorId.of(targetSector))
            .syncFullPlayer(true)
            .preload(true)
            .lockData(true)
            .execute()
            .thenAccept(result -> {
                if (result.isSuccess()) {
                    player.sendMessage("Transferring to sector: " + targetSector);
                } else {
                    player.sendMessage("Transfer failed: " + result.getErrorMessage());
                }
            });
        return true;
    }

    private boolean handleSync(Player player) {
        syncService.playerSync().syncFull(player.getUniqueId())
            .thenAccept(result -> player.sendMessage("Sync complete: " + result.getState()));
        return true;
    }

    private boolean handleSave(Player player) {
        ExampleUserRepository repo = service.repository(ExampleUserRepository.class);
        repo.getCached(player.getUniqueId()).ifPresent(user ->
            repo.save(user).thenRun(() -> player.sendMessage("Data saved."))
        );
        return true;
    }

    private boolean handleLoad(Player player) {
        ExampleUserRepository repo = service.repository(ExampleUserRepository.class);
        repo.invalidateCache(player.getUniqueId());
        repo.loadOrCreate(player.getUniqueId(), player.getName())
            .thenRun(() -> player.sendMessage("Data reloaded."));
        return true;
    }
}
