package pl.fejzu.persistence.sync;

public interface PlayerSyncSnapshot extends SyncSnapshot {

    String getPlayerName();

    String getLastServer();
}
