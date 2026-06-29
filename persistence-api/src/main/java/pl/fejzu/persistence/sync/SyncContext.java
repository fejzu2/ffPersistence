package pl.fejzu.persistence.sync;

import pl.fejzu.persistence.service.PersistenceContext;

import java.time.Instant;

public interface SyncContext {

    SyncService getSyncService();

    PersistenceContext getPersistenceContext();

    SyncPacket getPacket();

    SyncSource getSource();

    Instant getReceivedAt();
}
