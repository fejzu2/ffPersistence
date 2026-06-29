package pl.fejzu.persistence.examples.common.model;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.fejzu.persistence.entity.PlayerPersistenceEntity;
import pl.fejzu.persistence.sync.SyncScope;
import pl.fejzu.persistence.sync.SyncStrategy;
import pl.fejzu.persistence.sync.annotation.SyncIgnore;
import pl.fejzu.persistence.sync.annotation.SynchronizedEntity;
import pl.fejzu.persistence.sync.annotation.SynchronizedField;

import java.util.UUID;

@Entity("example_users")
@SynchronizedEntity(scope = SyncScope.SERVER)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public final class ExampleUser implements PlayerPersistenceEntity {

    @Id
    private UUID uniqueId;
    private String name;

    @SynchronizedField(strategy = SyncStrategy.INSTANT)
    private long coins;

    @SynchronizedField(strategy = SyncStrategy.DEBOUNCE, delayMs = 300)
    private int level;

    @SynchronizedField(strategy = SyncStrategy.INSTANT)
    private String rank;

    @SynchronizedField(strategy = SyncStrategy.INSTANT)
    private String lastSector;

    @SynchronizedField(strategy = SyncStrategy.INSTANT)
    private boolean vanished;

    @SynchronizedField(strategy = SyncStrategy.INSTANT)
    private String guildId;

    @SynchronizedField(strategy = SyncStrategy.BATCH)
    private long playTime;

    @SynchronizedField(strategy = SyncStrategy.DEBOUNCE, delayMs = 1000)
    private ExampleLocationData lastLocation;

    @SyncIgnore
    private transient long lastCacheAccess;
}
