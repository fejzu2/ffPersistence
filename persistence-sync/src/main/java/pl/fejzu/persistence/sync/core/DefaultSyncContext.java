package pl.fejzu.persistence.sync.core;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pl.fejzu.persistence.service.PersistenceContext;
import pl.fejzu.persistence.sync.SyncContext;
import pl.fejzu.persistence.sync.SyncPacket;
import pl.fejzu.persistence.sync.SyncService;
import pl.fejzu.persistence.sync.SyncSource;

import java.time.Instant;

@Getter
@RequiredArgsConstructor
public final class DefaultSyncContext implements SyncContext {

    private final SyncService syncService;
    private final PersistenceContext persistenceContext;
    private final SyncPacket packet;
    private final SyncSource source;
    private final Instant receivedAt;

    public static DefaultSyncContext of(SyncService syncService, PersistenceContext persistenceContext, SyncPacket packet) {
        SyncSource source = packet.getSourceServer() != null
            ? SyncSource.player(packet.getPlayerId(), packet.getSourceServer())
            : SyncSource.of("unknown");
        return new DefaultSyncContext(syncService, persistenceContext, packet, source, Instant.now());
    }
}
