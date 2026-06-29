package pl.fejzu.persistence.examples.common.sync;

import pl.fejzu.persistence.examples.common.model.ExampleGuild;
import pl.fejzu.persistence.examples.common.model.ExampleUser;
import pl.fejzu.persistence.sync.SyncScope;
import pl.fejzu.persistence.sync.SyncService;

public final class ExampleSyncFactory {

    private ExampleSyncFactory() {}

    public static void registerAll(SyncService syncService) {
        registerPlayerSync(syncService);
        registerGuildSync(syncService);
        registerCustomPackets(syncService);
    }

    public static void registerPlayerSync(SyncService syncService) {
        syncService.registry().entity(ExampleUser.class)
            .scope(SyncScope.SERVER)
            .register();
    }

    public static void registerGuildSync(SyncService syncService) {
        syncService.registry().entity(ExampleGuild.class)
            .scope(SyncScope.SERVER)
            .register();
    }

    public static void registerCustomPackets(SyncService syncService) {
        syncService.custom("combat-tag")
            .handler(CombatTagSyncPacket.class, ExampleSyncHandlers::handleCombatTag)
            .codec(CombatTagSyncPacket.class, new ExampleSyncHandlers.CombatTagCodec())
            .register();

        syncService.custom("coins-update")
            .handler(CoinsUpdatePacket.class, ExampleSyncHandlers::handleCoinsUpdate)
            .codec(CoinsUpdatePacket.class, new ExampleSyncHandlers.CoinsUpdateCodec())
            .register();

        syncService.custom("guild-sync")
            .handler(GuildSyncPacket.class, ExampleSyncHandlers::handleGuildSync)
            .codec(GuildSyncPacket.class, new ExampleSyncHandlers.GuildSyncCodec())
            .register();
    }
}
