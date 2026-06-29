package pl.fejzu.persistence.sync;

public interface SyncEntityRegistrationBuilder {

    SyncEntityRegistrationBuilder scope(SyncScope scope);

    SyncEntityRegistrationBuilder defaultStrategy(SyncStrategy strategy);

    SyncEntityRegistrationBuilder direction(SyncDirection direction);

    SyncEntityRegistrationBuilder field(String name, SyncStrategy strategy);

    SyncEntityRegistrationBuilder field(String name, SyncStrategy strategy, long delayMs);

    SyncEntityRegistrationBuilder field(String name, SyncStrategy strategy, long delayMs, SyncScope scope);

    SyncEntity register();
}
