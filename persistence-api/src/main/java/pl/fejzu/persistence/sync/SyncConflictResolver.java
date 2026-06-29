package pl.fejzu.persistence.sync;

public interface SyncConflictResolver {

    SyncSnapshot resolve(SyncSnapshot local, SyncSnapshot incoming, SyncConflictStrategy strategy);
}
