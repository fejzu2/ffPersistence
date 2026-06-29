package pl.fejzu.persistence.sync.core;

import pl.fejzu.persistence.sync.SyncContext;
import pl.fejzu.persistence.sync.SyncPacket;
import pl.fejzu.persistence.sync.SyncPacketCodec;
import pl.fejzu.persistence.sync.SyncPacketHandler;
import pl.fejzu.persistence.sync.SyncPacketRegistry;
import pl.fejzu.persistence.sync.SyncService;

@SuppressWarnings("unchecked")
public final class SyncPacketDispatcher {

    private final SyncPacketRegistry packetRegistry;
    private final SyncService syncService;

    public SyncPacketDispatcher(SyncPacketRegistry packetRegistry, SyncService syncService) {
        this.packetRegistry = packetRegistry;
        this.syncService = syncService;
    }

    public void dispatch(String typeId, byte[] data) {
        SyncPacketCodec<SyncPacket> codec = (SyncPacketCodec<SyncPacket>) packetRegistry.getCodec(typeId).orElse(null);
        SyncPacketHandler<SyncPacket> handler = (SyncPacketHandler<SyncPacket>) packetRegistry.getHandler(typeId).orElse(null);
        if (codec == null || handler == null) return;
        try {
            SyncPacket packet = codec.decode(data);
            SyncContext context = DefaultSyncContext.of(syncService, syncService.registry() instanceof DefaultSyncRegistry ? null : null, packet);
            handler.handle(packet, context);
        } catch (Exception ignored) {
        }
    }
}
