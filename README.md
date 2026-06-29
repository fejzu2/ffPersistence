# ffPersistence

![License](https://img.shields.io/github/license/fejzu/ffpersistence?style=for-the-badge&color=blue)
![Java](https://img.shields.io/badge/java-25+-blue.svg?style=for-the-badge)
![Platform](https://img.shields.io/badge/platform-paper%20%7C%20velocity%20%7C%20bungeecord-blue.svg?style=for-the-badge)

Persistence, cache and cross-server communication library for Minecraft plugins 1.21+. Add it as a dependency, shade it into your jar — no extra plugins on the server required.

Works like HikariCP or Caffeine: create an object, call `register()`, done.

## Features

- **Repository Pattern** — `PersistenceRepository<T, ID>` abstraction with MongoDB, cache and async out of the box
- **Async by Design** — all I/O returns `CompletableFuture`, never blocks the main thread
- **Transparent Cache** — `MemoryCacheProvider` sits between repository and storage with configurable TTL and eviction
- **MongoDB + Morphia** — `AbstractMongoRepository` backed by Morphia 2.5, annotate your entity and get full CRUD for free
- **Player Lifecycle** — optional bootstrap listener auto-loads on join, auto-saves on quit
- **Loading Display** — BossBar and ActionBar with animated frames, driven by `LoadingSettings`
- **Plugin Messaging** — binary packet protocol over `ffpersistence:*` channels, shared by Paper, Velocity and BungeeCord
- **Redis Pub/Sub** — `RedisCommunicationBus` for real-time cross-instance messaging
- **NATS Messaging** — `NatsCommunicationBus` for publish/subscribe with subject routing
- **Proxy Queue** — Velocity and BungeeCord bootstraps hold the player transfer until the backend confirms data is ready
- **Cross-Server Sync** — `persistence-sync` synchronises player and entity data across servers via CommunicationBus; reflection-driven field scanning with `@SynchronizedField`, debounce/batch strategies, custom packet codec registration
- **Sector Management** — `persistence-sector` divides the network into named sectors with priority-aware selection, lock-safe transfer, preload coordination and a fluent `transfer(uuid).to("spawn-2").execute()` API
- **Proxy Sector Integration** — Velocity and BungeeCord bootstraps auto-route joining players to the right sector server via `PlayerChooseInitialServerEvent` / `PostLoginEvent`
- **Dirty Tracking** — mark entities dirty, flush only changed records
- **Graceful Shutdown** — `LifecycleService` ensures all pending saves complete before stopping
- **Unified Settings** — immutable `PersistenceSettings` with builder, `.properties` file and environment variable providers
- **Pure Library** — shade it into your plugin; no server-side installation required

## Requirements

| Requirement | Version |
|-------------|---------|
| Java | 25+ |
| Paper / Spigot | 1.21+ (for `persistence-paper`) |
| Velocity | 3.4+ (for `persistence-velocity`) |
| BungeeCord | 1.21+ (for `persistence-bungee`) |
| MongoDB | 6+ |
| Redis | 6+ (optional) |
| NATS | 2.10+ (optional) |

## Installation

Add the repository and shade the modules you need into your plugin jar.

### Maven

```xml
<repositories>
    <repository>
        <id>fejzu-repo</id>
        <url>https://repo.fejzu.pl/releases</url>
    </repository>
</repositories>

<dependencies>
    <!-- Always required -->
    <dependency>
        <groupId>pl.fejzu.persistence</groupId>
        <artifactId>persistence-api</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </dependency>
    <dependency>
        <groupId>pl.fejzu.persistence</groupId>
        <artifactId>persistence-core</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </dependency>

    <!-- Storage -->
    <dependency>
        <groupId>pl.fejzu.persistence</groupId>
        <artifactId>persistence-mongodb</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </dependency>

    <!-- Platform integration (pick what you need) -->
    <dependency>
        <groupId>pl.fejzu.persistence</groupId>
        <artifactId>persistence-paper</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </dependency>
    <!-- or persistence-velocity / persistence-bungee -->

    <!-- Cross-server sync (optional) -->
    <dependency>
        <groupId>pl.fejzu.persistence</groupId>
        <artifactId>persistence-sync</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </dependency>

    <!-- Sector management (optional, requires persistence-sync) -->
    <dependency>
        <groupId>pl.fejzu.persistence</groupId>
        <artifactId>persistence-sector</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </dependency>

    <!-- Optional communication -->
    <dependency>
        <groupId>pl.fejzu.persistence</groupId>
        <artifactId>persistence-redis</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </dependency>
    <dependency>
        <groupId>pl.fejzu.persistence</groupId>
        <artifactId>persistence-nats</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </dependency>
</dependencies>
```

Shade everything with maven-shade-plugin:

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-shade-plugin</artifactId>
    <configuration>
        <relocations>
            <relocation>
                <pattern>pl.fejzu.persistence</pattern>
                <shadedPattern>com.example.myplugin.libs.persistence</shadedPattern>
            </relocation>
        </relocations>
    </configuration>
</plugin>
```

### Gradle (Kotlin DSL)

```kotlin
repositories {
    maven("https://repo.fejzu.pl/releases")
}

dependencies {
    // Always required
    implementation("pl.fejzu.persistence:persistence-api:1.0.0-SNAPSHOT")
    implementation("pl.fejzu.persistence:persistence-core:1.0.0-SNAPSHOT")

    // Storage
    implementation("pl.fejzu.persistence:persistence-mongodb:1.0.0-SNAPSHOT")

    // Platform integration (pick what you need)
    implementation("pl.fejzu.persistence:persistence-paper:1.0.0-SNAPSHOT")
    // or persistence-velocity / persistence-bungee

    // Cross-server sync (optional)
    implementation("pl.fejzu.persistence:persistence-sync:1.0.0-SNAPSHOT")

    // Sector management (optional, requires persistence-sync)
    implementation("pl.fejzu.persistence:persistence-sector:1.0.0-SNAPSHOT")

    // Optional communication
    implementation("pl.fejzu.persistence:persistence-redis:1.0.0-SNAPSHOT")
    implementation("pl.fejzu.persistence:persistence-nats:1.0.0-SNAPSHOT")
}
```

Shade with Shadow plugin:

```kotlin
plugins {
    id("com.gradleup.shadow") version "8.3.0"
}

tasks.shadowJar {
    relocate("pl.fejzu.persistence", "com.example.myplugin.libs.persistence")
}
```

---

## Quick Start

### 1. Define Your Entity

```java
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.fejzu.persistence.entity.PlayerPersistenceEntity;

@Entity("users")
@Getter @Setter @NoArgsConstructor
public final class UserData implements PlayerPersistenceEntity {

    @Id private UUID uniqueId;
    private String name;
    private long coins;
    private int level;

    public UserData(UUID uniqueId, String name) {
        this.uniqueId = uniqueId;
        this.name = name;
    }
}
```

### 2. Create Your Repository

```java
import pl.fejzu.persistence.mongodb.repository.AbstractMongoRepository;
import pl.fejzu.persistence.service.PersistenceContext;

public final class UserRepository extends AbstractMongoRepository<UserData, UUID> {

    public UserRepository(PersistenceContext context) {
        super(context, UserData.class);
    }

    public CompletableFuture<LoadResult<UserData>> loadOrCreate(UUID id, String name) {
        return load(id).thenCompose(result -> {
            if (result.isSuccess()) return CompletableFuture.completedFuture(result);
            UserData newUser = new UserData(id, name);
            return save(newUser).thenApply(r -> LoadResult.success(newUser, DataState.LOADED));
        });
    }
}
```

### 3. Bootstrap in Your Plugin

```java
public final class MyPlugin extends JavaPlugin {

    private PersistenceService persistence;
    private PaperPersistenceBootstrap bootstrap;

    @Override
    public void onEnable() {
        MongoStorageProvider storage = new MongoStorageProvider(
            MongoConfiguration.of("mongodb://localhost:27017", "my_server")
        );
        storage.connect().join();

        persistence = DefaultPersistenceService.builder()
            .storage(storage)
            .settings(PersistenceSettings.defaults())
            .build();

        UserRepository users = persistence.repository(UserRepository.class);

        bootstrap = new PaperPersistenceBootstrap(this, persistence)
            .playerLoader(new MyPlayerDataLoader(users))
            .register();
    }

    @Override
    public void onDisable() {
        if (bootstrap != null) bootstrap.unregister();
        if (persistence != null) persistence.shutdown();
    }
}
```

### 4. Implement PlayerDataLoader

```java
public final class MyPlayerDataLoader implements PlayerDataLoader {

    private final UserRepository users;

    public MyPlayerDataLoader(UserRepository users) {
        this.users = users;
    }

    @Override
    public CompletableFuture<Void> loadPlayer(UUID id, String name, LoadReason reason) {
        return users.loadOrCreate(id, name).thenAccept(result -> {
            if (result.isFailure()) {
                Bukkit.getLogger().severe("Failed to load player: " + name);
            }
        });
    }

    @Override
    public CompletableFuture<Void> savePlayer(UUID id, SaveReason reason) {
        return users.getCached(id)
            .map(users::save)
            .orElse(CompletableFuture.completedFuture(null))
            .thenAccept(r -> {});
    }

    @Override
    public CompletableFuture<Void> unloadPlayer(UUID id, SaveReason reason) {
        return savePlayer(id, reason)
            .thenRun(() -> users.invalidateCache(id));
    }
}
```

### 5. Use the Repository

```java
// Load
users.load(player.getUniqueId())
    .thenAccept(result -> {
        if (result.isSuccess()) {
            player.sendMessage("Coins: " + result.getData().getCoins());
        }
    });

// Modify in-place (from cache)
users.getCached(player.getUniqueId()).ifPresent(user -> {
    user.setCoins(user.getCoins() + 100);
    users.save(user);
});

// Delete
users.delete(player.getUniqueId());
```

---

## Module Overview

| Module | Purpose | Key Types |
|--------|---------|-----------|
| `persistence-api` | Interfaces, settings, results | `PersistenceService`, `PersistenceRepository`, `PersistenceSettings` |
| `persistence-core` | Default implementations | `DefaultPersistenceService`, `AbstractPersistenceRepository`, `MemoryCacheProvider` |
| `persistence-mongodb` | MongoDB via Morphia 2.5 | `AbstractMongoRepository`, `MongoStorageProvider`, `MongoConfiguration` |
| `persistence-plugin-messaging` | Binary packet protocol | `PluginMessagingBus`, `LoadPlayerPacket`, `PlayerReadyPacket` |
| `persistence-redis` | Redis pub/sub | `RedisCommunicationBus`, `RedisConfiguration` |
| `persistence-nats` | NATS publish/subscribe | `NatsCommunicationBus`, `NatsConfiguration` |
| `persistence-sync` | Cross-server data sync | `DefaultSyncService`, `PlayerSyncService`, `@SynchronizedField` |
| `persistence-sector` | Sector / server-zone management | `DefaultSectorService`, `SectorTransfer`, `DefaultSectorConnectService` |
| `persistence-paper` | Paper/Spigot bootstrap | `PaperPersistenceBootstrap`, `PaperBossBarLoadingDisplay` |
| `persistence-velocity` | Velocity proxy bootstrap | `VelocityPersistenceBootstrap`, `VelocitySectorIntegration` |
| `persistence-bungee` | BungeeCord proxy bootstrap | `BungeePersistenceBootstrap`, `BungeeSectorIntegration` |

None of these modules ship a `plugin.yml`, `velocity-plugin.json` or `bungee.yml`. They are plain Java libraries.

---

## Bootstrap

Each platform has a lightweight bootstrap class. It registers listeners, plugin messaging channels and the lifecycle service inside the user's own plugin. No base class to extend.

### Paper / Spigot

```java
PaperPersistenceBootstrap bootstrap = new PaperPersistenceBootstrap(this, service)
    .playerLoader(myLoader)   // optional — omit to skip lifecycle listener
    .register();

// On disable
bootstrap.unregister();
```

`PaperPersistenceBootstrap.register()` does:
1. Starts `LifecycleService` (graceful shutdown queue)
2. Registers `PaperPlayerLifecycleListener` (if `playerLoader` is set)
3. Registers Plugin Messaging channels via `PaperPluginMessagingBridge`
4. Creates `PaperLoadingAnimationTask` when `LoadingSettings.enabled = true`

### Velocity

```java
VelocityPersistenceBootstrap bootstrap =
    new VelocityPersistenceBootstrap(this, proxy, service)
        .sectorIntegration(true)   // optional — enables automatic sector routing on join
        .register();

// On proxy shutdown
bootstrap.unregister();
```

`register()` starts the lifecycle and registers the messaging event listener on the Velocity event bus. When `sectorIntegration(true)` is called and `SectorSettings.proxyAutoConnectEnabled = true`, a `VelocitySectorJoinListener` is registered that intercepts `PlayerChooseInitialServerEvent` and routes the player to the sector-selected server asynchronously.

### BungeeCord

```java
BungeePersistenceBootstrap bootstrap =
    new BungeePersistenceBootstrap(this, service)
        .sectorIntegration(true)   // optional
        .register();

// On disable
bootstrap.unregister();
```

When `sectorIntegration(true)` is set and `proxyAutoConnectEnabled = true`, a `BungeeSectorJoinListener` is registered on `PostLoginEvent` and calls `player.connect(serverInfo)` with the resolved sector server.

---

## Settings

All framework behaviour is controlled by `PersistenceSettings`. Pass it to the service builder and it flows to all bootstraps automatically.

### Programmatic

```java
PersistenceSettings settings = PersistenceSettings.builder()
    .cache(CacheSettings.builder()
        .expireAfterWrite(Duration.ofMinutes(60))
        .maxSize(5000)
        .build())
    .loading(LoadingSettings.builder()
        .displayType(LoadingDisplayType.BOSS_BAR)
        .bossBarColor(BossBarColor.GREEN)
        .title("Loading your data...")
        .loadTimeoutSeconds(10)
        .build())
    .playerLifecycle(PlayerLifecycleSettings.builder()
        .loadTimeoutSeconds(10)
        .kickOnLoadFailure(true)
        .kickMessage("Failed to load your player data. Please reconnect.")
        .saveInterval(Duration.ofMinutes(5))
        .build())
    .proxy(ProxySettings.builder()
        .enabled(true)
        .platform(ProxyPlatform.VELOCITY)
        .transferTimeoutSeconds(15)
        .fallbackServer("lobby")
        .build())
    .sync(SyncSettings.builder()
        .enabled(true)
        .defaultStrategy(SyncStrategy.DEBOUNCE)
        .debounceDelayMs(300)
        .build())
    .sector(SectorSettings.builder()
        .enabled(true)
        .currentSector("survival-1")
        .currentServerName("survival-1")
        .joinStrategy(SectorJoinStrategy.LAST_SECTOR)
        .fallbackSector("lobby")
        .lobbySector("lobby")
        .proxyAutoConnectEnabled(true)
        .build())
    .build();

DefaultPersistenceService.builder()
    .storage(storage)
    .settings(settings)
    .build();
```

### Static Presets

```java
PersistenceSettings.defaults()
LoadingSettings.defaults()           // BossBar, DOTS, 10s timeout
LoadingSettings.disabled()           // no display
LoadingSettings.actionBar()          // ACTION_BAR type
CacheSettings.defaults()             // 30 min TTL, 1000 max entries
CacheSettings.disabled()
CacheSettings.aggressive()           // 2h TTL, 10 000 max
PlayerLifecycleSettings.defaults()
PlayerLifecycleSettings.noAutoSave() // manual save only
ProxySettings.velocity()             // enabled, VELOCITY, 15s
ProxySettings.bungeeCord()           // enabled, BUNGEECORD, 15s
StorageSettings.defaults()
StorageSettings.lenient()            // longer timeouts, more retries
SyncSettings.enabled()               // enabled, INSTANT strategy
SectorSettings.enabled("survival")   // enabled, sector = "survival"
```

### File-Based (`persistence.properties`)

```java
DefaultPersistenceService.builder()
    .storage(storage)
    .settingsProvider(new FileSettingsProvider(
        Path.of(getDataFolder().getPath(), "persistence.properties")
    ))
    .build();
```

```properties
cache.enabled=true
cache.max-size=3000
cache.expire-after-write-minutes=60
cache.expire-after-access-minutes=20

loading.enabled=true
loading.title=Loading your data...
loading.display-type=BOSS_BAR
loading.boss-bar-color=GREEN
loading.timeout-seconds=10
loading.hide-on-success=true
loading.message.error=Failed to load. Please reconnect.
loading.message.kick=Failed to load your player data.

lifecycle.load-timeout-seconds=10
lifecycle.kick-on-load-failure=true
lifecycle.kick-message=Failed to load your player data. Please reconnect.
lifecycle.save-interval-minutes=5

proxy.enabled=true
proxy.transfer-timeout-seconds=15
proxy.fallback-server=lobby

sync.enabled=true
sync.default-strategy=INSTANT
sync.debounce-delay-ms=500
sync.batch-interval-seconds=5
sync.conflict-strategy=LAST_WRITE_WINS

sector.enabled=true
sector.current=survival-1
sector.transfer-timeout-seconds=15
sector.preload-target=true
sector.lock-player-data=true
```

### Environment Variables

```java
DefaultPersistenceService.builder()
    .storage(storage)
    .settingsProvider(new EnvironmentSettingsProvider())
    .build();
```

| Variable | Field |
|----------|-------|
| `FFPERSISTENCE_CACHE_ENABLED` | `cache.enabled` |
| `FFPERSISTENCE_CACHE_MAX_SIZE` | `cache.maxSize` |
| `FFPERSISTENCE_CACHE_EXPIRE_AFTER_WRITE_MINUTES` | `cache.expireAfterWrite` |
| `FFPERSISTENCE_LOADING_ENABLED` | `loading.enabled` |
| `FFPERSISTENCE_LOADING_TITLE` | `loading.title` |
| `FFPERSISTENCE_LOADING_DISPLAY_TYPE` | `BOSS_BAR`, `ACTION_BAR`, `TITLE`, `CHAT`, `NONE` |
| `FFPERSISTENCE_LOADING_TIMEOUT_SECONDS` | `loading.loadTimeoutSeconds` |
| `FFPERSISTENCE_LIFECYCLE_LOAD_TIMEOUT_SECONDS` | `playerLifecycle.loadTimeoutSeconds` |
| `FFPERSISTENCE_LIFECYCLE_KICK_ON_LOAD_FAILURE` | `playerLifecycle.kickOnLoadFailure` |
| `FFPERSISTENCE_LIFECYCLE_KICK_MESSAGE` | `playerLifecycle.kickMessage` |
| `FFPERSISTENCE_LIFECYCLE_SAVE_INTERVAL_MINUTES` | `playerLifecycle.saveInterval` |
| `FFPERSISTENCE_PROXY_ENABLED` | `proxy.enabled` |
| `FFPERSISTENCE_PROXY_TRANSFER_TIMEOUT_SECONDS` | `proxy.transferTimeoutSeconds` |
| `FFPERSISTENCE_PROXY_FALLBACK_SERVER` | `proxy.fallbackServer` |
| `FFPERSISTENCE_STORAGE_CONNECT_TIMEOUT_MS` | `storage.connectTimeoutMs` |
| `FFPERSISTENCE_STORAGE_MAX_POOL_SIZE` | `storage.maxPoolSize` |
| `FFPERSISTENCE_COMMUNICATION_ENABLED` | `communication.enabled` |
| `FFPERSISTENCE_COMMUNICATION_DEBUG_PACKETS` | `communication.debugPackets` |

### Merging Sources

```java
// defaults → file → env (env has highest priority)
PersistenceSettings settings = SettingsMerger.builder()
    .withFile(Path.of(getDataFolder().getPath(), "persistence.properties"))
    .withEnvironment()
    .build();
```

### Validating

```java
SettingsValidator validator = new SettingsValidator();

ValidationResult result = validator.validate(settings);
result.getErrors().forEach(e -> getLogger().severe("[config] " + e));
result.getWarnings().forEach(w -> getLogger().warning("[config] " + w));

// throws InvalidSettingsException if any errors are found
validator.validateOrThrow(settings);
```

### All Settings Fields

| Sub-settings | Field | Default |
|-------------|-------|---------|
| **LoadingSettings** | `enabled` | `true` |
| | `title` | `"Loading..."` |
| | `subtitle` | `"Please wait"` |
| | `animation` | `DOTS` |
| | `displayType` | `BOSS_BAR` |
| | `bossBarColor` | `BLUE` |
| | `bossBarStyle` | `PROGRESS` |
| | `animationIntervalMs` | `100` |
| | `loadTimeoutSeconds` | `10` |
| | `hideOnSuccess` | `true` |
| **LoadingMessages** | `loading` | `"Loading your data..."` |
| | `success` | `"Your data has been loaded!"` |
| | `error` | `"Failed to load your data. Please try reconnecting."` |
| | `timeout` | `"Data load timed out. Please try reconnecting."` |
| | `kick` | `"Failed to load your player data. Please try again."` |
| **CacheSettings** | `enabled` | `true` |
| | `expireAfterWrite` | `30 min` |
| | `expireAfterAccess` | `10 min` |
| | `maxSize` | `1000` |
| | `saveOnEvict` | `true` |
| | `cleanupInterval` | `5 min` |
| **StorageSettings** | `connectTimeoutMs` | `5000` |
| | `socketTimeoutMs` | `30000` |
| | `maxPoolSize` | `20` |
| | `autoRetry` | `true` |
| | `maxRetries` | `3` |
| | `retryDelayMs` | `1000` |
| **CommunicationSettings** | `enabled` | `false` |
| | `defaultBus` | `"plugin-messaging"` |
| | `requestTimeoutMs` | `5000` |
| | `channelsPrefix` | `"ffpersistence"` |
| | `debugPackets` | `false` |
| **PlayerLifecycleSettings** | `loadOnJoin` | `true` |
| | `saveOnQuit` | `true` |
| | `unloadOnQuit` | `true` |
| | `saveOnShutdown` | `true` |
| | `saveInterval` | `5 min` |
| | `loadTimeoutSeconds` | `10` |
| | `kickOnLoadFailure` | `true` |
| | `kickMessage` | `"Failed to load your player data."` |
| **ProxySettings** | `enabled` | `false` |
| | `platform` | `NONE` |
| | `waitForBackendReady` | `true` |
| | `transferTimeoutSeconds` | `15` |
| | `fallbackServer` | `"lobby"` |
| **QueueSettings** | `enabled` | `true` |
| | `showPosition` | `true` |
| | `preloadWhenFirst` | `true` |
| | `loadingMessage` | `"Your data is being loaded..."` |
| **SyncSettings** | `enabled` | `false` |
| | `autoPlayerSync` | `true` |
| | `syncOnJoin` | `true` |
| | `syncOnQuit` | `true` |
| | `syncOnTransfer` | `true` |
| | `syncDirtyOnly` | `true` |
| | `defaultStrategy` | `INSTANT` |
| | `defaultScope` | `SERVER` |
| | `conflictStrategy` | `LAST_WRITE_WINS` |
| | `debounceDelayMs` | `500` |
| | `batchInterval` | `5 s` |
| | `includeTransientFields` | `false` |
| | `maxPacketSize` | `65536` |
| | `compressionEnabled` | `false` |
| **SectorSettings** | `enabled` | `false` |
| | `currentSector` | `"default"` |
| | `currentServerName` | `""` |
| | `transferTimeoutSeconds` | `15` |
| | `preloadTarget` | `true` |
| | `lockPlayerDataDuringTransfer` | `true` |
| | `fullSyncOnTransfer` | `true` |
| | `unloadSourceCacheAfterTransfer` | `false` |
| | `fallbackSector` | `"lobby"` |
| | `lobbySector` | `"lobby"` |
| | `joinStrategy` | `LAST_SECTOR` |
| | `connectToLastSectorOnJoin` | `false` |
| | `saveLastSectorOnQuit` | `true` |
| | `updateLastSectorOnTransfer` | `true` |
| | `allowFallbackWhenLastOffline` | `true` |
| | `validateLastSectorOnline` | `true` |
| | `proxyAutoConnectEnabled` | `false` |
| | `borderDetectionEnabled` | `false` |
| | `borderCheckIntervalTicks` | `20` |

---

## Entity System

### PersistenceEntity

```java
public interface PersistenceEntity<ID> {
    ID getId();
}
```

### PlayerPersistenceEntity

Pre-keyed by `UUID` for player data:

```java
public interface PlayerPersistenceEntity extends PersistenceEntity<UUID> {
    UUID getUniqueId();
    String getName();
}
```

### Custom ID Types

```java
@Entity("guilds")
@Getter @Setter @NoArgsConstructor
public final class GuildData implements PersistenceEntity<String> {

    @Id private String id; // guild tag
    private String displayName;
    private List<UUID> members = new ArrayList<>();

    @Override public String getId() { return id; }
}

public final class GuildRepository extends AbstractMongoRepository<GuildData, String> {
    public GuildRepository(PersistenceContext context) {
        super(context, GuildData.class);
    }
}
```

---

## Repository Pattern

### Core Interface

```java
public interface PersistenceRepository<T extends PersistenceEntity<ID>, ID> {
    CompletableFuture<LoadResult<T>> load(ID id);
    CompletableFuture<SaveResult> save(T entity);
    CompletableFuture<DeleteResult> delete(ID id);
    CompletableFuture<Boolean> exists(ID id);
    CompletableFuture<List<T>> findAll();
    CompletableFuture<Void> saveAll(List<T> entities);
}
```

### Transparent Cache

`AbstractPersistenceRepository` handles caching without any effort from the caller:

```java
userRepo.load(uuid).thenAccept(result -> {
    // result.getDataState() == DataState.CACHED  — served from memory
    // result.getDataState() == DataState.LOADED  — fetched from MongoDB
});
```

### MongoDB Queries

Add custom queries with Morphia `Filters`:

```java
public final class UserRepository extends AbstractMongoRepository<UserData, UUID> {

    public UserRepository(PersistenceContext context) {
        super(context, UserData.class);
    }

    public CompletableFuture<Optional<UserData>> findByName(String name) {
        return findOneBy(Filters.eq("name", name));
    }

    public CompletableFuture<List<UserData>> topByCoins(int limit) {
        return CompletableFuture.supplyAsync(() ->
            datastore.find(UserData.class)
                .stream(new FindOptions().sort(Sort.descending("coins")).limit(limit))
                .toList(),
            getExecutor()
        );
    }
}
```

---

## Results

```java
LoadResult<UserData> r = userRepo.load(uuid).join();
r.isSuccess();          // true if loaded or cached
r.isFailure();          // true if not found or error
r.getData();            // entity or null
r.getDataState();       // LOADED | CACHED | NOT_FOUND | ERROR
r.asOptional();         // Optional<UserData>

SaveResult save = userRepo.save(user).join();
save.isSuccess();
save.getCause();        // Throwable if error

DeleteResult del = userRepo.delete(uuid).join();
del.getStatus();        // SUCCESS | NOT_FOUND | ERROR
```

---

## Loading Display

Configured entirely through `LoadingSettings`. No manual wiring required — `PaperPersistenceBootstrap` creates the display and animation task from settings automatically.

To use a custom display type per session:

```java
PaperBossBarLoadingDisplay display = new PaperBossBarLoadingDisplay(getServer(), loadingSettings);
PaperLoadingAnimationTask animation = new PaperLoadingAnimationTask(
    this, display, LoadingAnimation.DOTS, "Loading...", 100
);

animation.startFor(player.getUniqueId());
// ... async work ...
animation.stopFor(player.getUniqueId());
```

### Available Animations

| Constant | Frames |
|----------|--------|
| `LoadingAnimation.DOTS` | `⠋ ⠙ ⠹ ⠸ ⠼ ⠴ ⠦ ⠧ ⠇ ⠏` |
| `LoadingAnimation.SPINNER` | `◜ ◝ ◞ ◟` |
| `LoadingAnimation.ARROWS` | `← ↖ ↑ ↗ → ↘ ↓ ↙` |
| `LoadingAnimation.BOUNCING` | `▁ ▃ ▄ ▅ ▆ ▇` |

### Display Types

| `LoadingDisplayType` | Implementation |
|---------------------|---------------|
| `BOSS_BAR` | `PaperBossBarLoadingDisplay` |
| `ACTION_BAR` | `PaperActionBarLoadingDisplay` |
| `NONE` | disabled |

### BossBar Colors & Styles

`BossBarColor`: `PINK`, `BLUE`, `RED`, `GREEN`, `YELLOW`, `PURPLE`, `WHITE`

`BossBarStyle`: `PROGRESS`, `NOTCHED_6`, `NOTCHED_10`, `NOTCHED_12`, `NOTCHED_20`

---

## Communication

### Redis

```java
RedisCommunicationBus bus = new RedisCommunicationBus(
    RedisConfiguration.builder()
        .host("localhost").port(6379).password("secret").build()
);
bus.connect();

bus.publish(CommunicationChannel.of(RedisChannel.PLAYER_READY), myMessage);
bus.subscribe(CommunicationChannel.of(RedisChannel.PLAYER_LOAD), message -> {
    // handle
});

DefaultPersistenceService.builder()
    .storage(storage)
    .communication(bus)
    .build();
```

### NATS

```java
NatsCommunicationBus bus = new NatsCommunicationBus(
    NatsConfiguration.builder().url("nats://localhost:4222").build()
);
bus.connect();
```

### Built-in Channels

| Constant | Channel / Subject |
|----------|------------------|
| `RedisChannel.PLAYER_LOAD` | `ffpersistence:player:load` |
| `RedisChannel.PLAYER_READY` | `ffpersistence:player:ready` |
| `RedisChannel.PLAYER_UNLOAD` | `ffpersistence:player:unload` |
| `NatsSubject.PLAYER_LOAD` | `ffpersistence.player.load` |
| `NatsSubject.PLAYER_READY` | `ffpersistence.player.ready` |

---

## Plugin Messaging

Binary protocol over Minecraft's built-in plugin messaging. Channels are registered by the bootstrap inside the user's plugin — no separate messaging plugin needed.

### Channels

| Channel | Direction | Purpose |
|---------|-----------|---------|
| `ffpersistence:main` | Proxy ↔ Backend | General control |
| `ffpersistence:load` | Proxy → Backend | Request player load |
| `ffpersistence:ready` | Backend → Proxy | Confirm data ready |
| `ffpersistence:save` | Both | Request save |
| `ffpersistence:sync` | Backend ↔ Backend | General sync packets |
| `ffpersistence:sync:player` | Backend ↔ Backend | Player sync packets |
| `ffpersistence:sync:entity` | Backend ↔ Backend | Entity sync packets |
| `ffpersistence:sector` | Proxy ↔ Backend | Sector control |
| `ffpersistence:sector:transfer` | Backend ↔ Backend | Transfer coordination |

### Packet Types

| Class | Type ID | Payload |
|-------|---------|---------|
| `LoadPlayerPacket` | `0x01` | UUID, player name, target server |
| `PlayerReadyPacket` | `0x02` | UUID, server name |
| `PlayerUnloadPacket` | `0x03` | UUID |
| `PlayerSavePacket` | `0x04` | UUID |
| `PlayerSectorChangePacket` | `0x05` | UUID, source sector, target sector |
| `PlayerTransferStartPacket` | `0x06` | UUID, player name, source, target, server |
| `PlayerTransferReadyPacket` | `0x07` | UUID, target sector, server name |
| `PlayerTransferCompletePacket` | `0x08` | UUID, target sector |
| `PlayerTransferFailedPacket` | `0x09` | UUID, target sector, reason |
| `ErrorPacket` | `0xFF` | UUID, error message |

### Sending Packets from Paper

```java
PaperPluginMessagingBridge bridge = bootstrap.getMessagingBridge();

bridge.registerHandler(PluginMessagingPacketType.LOAD_PLAYER, packet -> {
    LoadPlayerPacket load = (LoadPlayerPacket) packet;
    userRepo.loadOrCreate(load.getPlayerId(), load.getPlayerName()).thenRun(() ->
        bridge.sendPacket(PluginMessagingChannel.READY,
            new PlayerReadyPacket(load.getPlayerId(), "my-server"))
    );
});
```

---

## Cross-Server Sync

`persistence-sync` synchronises player and entity data across servers via the `CommunicationBus`. It is transport-agnostic — it works with Redis, NATS, or plugin messaging.

### Setup

```java
SyncService syncService = DefaultSyncService.builder()
    .context(persistence.getContext())
    .build()
    .register();

// Wire into the service so context.getSyncService() returns it
persistence = DefaultPersistenceService.builder()
    .storage(storage)
    .sync(syncService)
    .build();
```

### Annotate Your Entity

```java
@SynchronizedEntity(defaultStrategy = SyncStrategy.DEBOUNCE)
@Entity("users")
@Getter @Setter @NoArgsConstructor
public final class UserData implements PlayerPersistenceEntity {

    @Id private UUID uniqueId;
    private String name;

    @SynchronizedField(strategy = SyncStrategy.INSTANT)
    private long coins;

    @SynchronizedField(strategy = SyncStrategy.DEBOUNCE, delayMs = 300)
    private int level;

    @SyncIgnore
    private transient long lastSeenMs; // never synced
}
```

Field-level annotations override the entity default. Fields annotated `@SyncIgnore` (or not annotated at all when `syncDirtyOnly = true`) are skipped.

### Sync Strategies

| `SyncStrategy` | Behaviour |
|----------------|-----------|
| `INSTANT` | Publish immediately on every change |
| `DEBOUNCE` | Wait `debounceDelayMs` ms after the last change, then publish |
| `BATCH` | Collect changes and flush on `batchInterval` |
| `MANUAL` | Only sync when `playerSync.syncFull(uuid)` is called explicitly |
| `ON_QUIT` | Sync once when the player disconnects |
| `DISABLED` | Never sync this field |

### PlayerSyncService

```java
PlayerSyncService playerSync = persistence.getContext()
    .getPlayerSyncService().orElseThrow();

// Push all annotated fields to other servers
playerSync.syncFull(playerId);

// Push a single field
playerSync.syncField(playerId, "coins");

// Push to a specific server only
playerSync.push(playerId, "sector-2");
```

### Conflict Resolution

| `SyncConflictStrategy` | Behaviour |
|------------------------|-----------|
| `LAST_WRITE_WINS` | Most recent timestamp wins (default) |
| `FIRST_WRITE_WINS` | Earlier timestamp wins |
| `REJECT` | Discard conflicting incoming packet |
| `MERGE` | Merge incoming fields into local snapshot |

### Custom Packet Types

```java
syncService.custom("combat-tag")
    .handler((ctx, packet) -> {
        CustomSyncPacket p = (CustomSyncPacket) packet;
        // handle combat tag sync
    })
    .codec(new MyCombatTagCodec())
    .register();
```

### Sync Packet Types (built-in)

| `SyncPacketType` | Channel | Description |
|-----------------|---------|-------------|
| `PLAYER_FULL_SYNC` | `sync:player` | All annotated player fields |
| `PLAYER_FIELD_SYNC` | `sync:player` | Single player field |
| `ENTITY_FULL_SYNC` | `sync:entity` | All annotated entity fields |
| `ENTITY_FIELD_SYNC` | `sync:entity` | Single entity field |
| `PLAYER_SECTOR_CHANGE` | `sync` | Player moved to a different sector |
| `PLAYER_TRANSFER_START` | `sync` | Transfer initiated |
| `PLAYER_TRANSFER_READY` | `sync` | Target server ready |
| `PLAYER_TRANSFER_COMPLETE` | `sync` | Transfer confirmed |
| `PLAYER_TRANSFER_FAILED` | `sync` | Transfer error |
| `CUSTOM` | `sync` | User-defined packet |

---

## Sector Management

`persistence-sector` manages named server zones (sectors). Each sector has a name, server name, priority and online state. The module handles lock-safe player transfers, optional preloading and automatic join-routing.

### Registering Sectors

```java
SectorService sectorService = DefaultSectorService.builder()
    .context(persistence.getContext())
    .syncService(syncService)   // optional — enables data sync on transfer
    .build()
    .register();

// Register known sectors (typically on startup)
sectorService.registerSector(DefaultSector.builder()
    .id(SectorId.of("survival-1"))
    .serverName("survival-1")
    .priority(10)
    .state(SectorState.ONLINE)
    .build());

sectorService.registerSector(DefaultSector.builder()
    .id(SectorId.of("survival-2"))
    .serverName("survival-2")
    .priority(10)
    .state(SectorState.ONLINE)
    .build());

sectorService.registerSector(DefaultSector.builder()
    .id(SectorId.of("lobby"))
    .serverName("lobby")
    .priority(1)
    .state(SectorState.ONLINE)
    .build());
```

### Transferring Players

```java
// Fluent transfer API
sectorService.transfer(playerId)
    .to(SectorId.of("survival-2"))
    .syncFullPlayer(true)   // sync all data before transfer
    .preload(true)          // wait for target server to confirm ready
    .execute()
    .thenAccept(result -> {
        if (result.isSuccess()) {
            log.info("Transfer complete in " + result.getDuration().toMillis() + "ms");
        } else {
            log.warning("Transfer failed: " + result.getFailReason());
        }
    });
```

### Transfer Flow

1. Validate that the target sector is `ONLINE`
2. Acquire a `SectorLock` on the player (prevents concurrent transfers)
3. Sync all player data to the target server (if `syncFullPlayer = true`)
4. Send `PLAYER_TRANSFER_START` via `CommunicationBus`
5. Await `PLAYER_TRANSFER_READY` from the target server (with configurable timeout)
6. Update `SectorPlayerTracker` — moves player to target sector
7. Release the lock

The proxy side (`VelocitySectorIntegration` / `BungeeSectorIntegration`) intercepts the join event and connects the player to the appropriate physical server after the transfer completes.

### Join Strategies

`SectorJoinStrategy` controls which sector a player is routed to on network join.

| Strategy | Behaviour |
|----------|-----------|
| `LAST_SECTOR` | Reconnect to the last known sector (with online validation) |
| `FALLBACK_SECTOR` | Use the highest-priority online sector |
| `BEST_AVAILABLE` | Highest-priority online sector with capacity |
| `LOWEST_ONLINE` | Lowest-priority online sector (load balancing) |
| `RANDOM` | Random online sector |
| `CUSTOM` | Use `SectorConnectRequest.preferredSector` if online, else fallback |

`LAST_SECTOR` falls back through: last sector → `fallbackSector` setting → `lobbySector` setting → failure.

### Proxy Auto-Connect (Velocity)

```java
// SectorSettings
SectorSettings settings = SectorSettings.builder()
    .enabled(true)
    .joinStrategy(SectorJoinStrategy.LAST_SECTOR)
    .fallbackSector("lobby")
    .lobbySector("lobby")
    .allowFallbackWhenLastOffline(true)
    .proxyAutoConnectEnabled(true)   // auto-registers the join listener
    .build();

// Bootstrap
VelocityPersistenceBootstrap bootstrap =
    new VelocityPersistenceBootstrap(this, proxy, service)
        .sectorIntegration(true)
        .register();
```

With `proxyAutoConnectEnabled = true` and `sectorIntegration(true)`, a `VelocitySectorJoinListener` is auto-registered. It intercepts `PlayerChooseInitialServerEvent` via `EventTask.async()`, resolves the target sector, and sets the initial server before the player connects.

### Proxy Auto-Connect (BungeeCord)

```java
BungeePersistenceBootstrap bootstrap =
    new BungeePersistenceBootstrap(this, service)
        .sectorIntegration(true)
        .register();
```

`BungeeSectorJoinListener` fires on `PostLoginEvent` and calls `player.connect(serverInfo)` asynchronously with the resolved server.

### Manual Connect Service

If you need direct control:

```java
DefaultSectorService sectorService = ...; // cast or use the concrete type

DefaultSectorConnectService connectService =
    sectorService.buildConnectService(new MemoryLastSectorProvider());

SectorConnectRequest request = SectorConnectRequest.builder()
    .playerId(uuid)
    .playerName("Steve")
    .strategy(SectorJoinStrategy.LAST_SECTOR)
    .preload(true)
    .timeoutMs(10_000)
    .build();

connectService.connectOnJoin(request)
    .thenAccept(result -> {
        if (result.isSuccess()) {
            proxy.getServer(result.getServerName())
                .ifPresent(s -> player.createConnectionRequest(s).fireAndForget());
        }
    });
```

### Last Sector Providers

`LastSectorProvider` persists which sector a player last visited. Two built-in implementations are available:

| Class | Storage |
|-------|---------|
| `MemoryLastSectorProvider` | In-memory `ConcurrentHashMap` — lost on restart |
| `RepositoryLastSectorProvider` | Delegates to any `CompletableFuture`-based loader/saver |

```java
// Backed by a repository field
LastSectorProvider provider = new RepositoryLastSectorProvider(
    uuid -> userRepo.load(uuid).thenApply(r -> r.asOptional()
        .map(u -> SectorId.of(u.getLastSector()))),
    (uuid, sectorId) -> userRepo.getCached(uuid)
        .map(u -> { u.setLastSector(sectorId.getValue()); return userRepo.save(u).thenAccept(x -> {}); })
        .orElse(CompletableFuture.completedFuture(null))
);
```

---

## Proxy Integration

### Velocity

```java
@Plugin(id = "myplugin")
public final class MyVelocityPlugin {

    @Inject private ProxyServer proxy;
    private VelocityPersistenceBootstrap bootstrap;

    @Subscribe
    public void onProxyInitialize(ProxyInitializeEvent event) {
        PersistenceService service = buildService();

        bootstrap = new VelocityPersistenceBootstrap(this, proxy, service)
            .sectorIntegration(true)
            .register();
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        bootstrap.unregister();
        service.shutdown();
    }

    public void transferPlayer(UUID playerId, String server) {
        bootstrap.getPlayerTransferService()
            .transferPlayer(playerId, server)
            .exceptionally(e -> { getLogger().error(e.getMessage()); return null; });
    }
}
```

**Transfer flow:**
1. `transferPlayer()` sends `LoadPlayerPacket` to target backend via plugin messaging
2. `VelocityQueueService` waits for `PlayerReadyPacket` (configurable timeout via `ProxySettings`)
3. Player is connected to the target server

### BungeeCord

```java
public final class MyBungeePlugin extends Plugin {

    private PersistenceService service;
    private BungeePersistenceBootstrap bootstrap;

    @Override
    public void onEnable() {
        service = buildService();

        bootstrap = new BungeePersistenceBootstrap(this, service)
            .sectorIntegration(true)
            .register();
    }

    @Override
    public void onDisable() {
        bootstrap.unregister();
        service.shutdown();
    }
}
```

---

## MongoDB Configuration

```java
MongoConfiguration config = MongoConfiguration.builder()
    .connectionString("mongodb://user:pass@localhost:27017")
    .database("my_server")
    .entityPackages(List.of("com.example.model"))
    .autoEnsureIndexes(true)
    .maxPoolSize(20)
    .build();

MongoStorageProvider storage = new MongoStorageProvider(config);
storage.connect().join();
```

### Morphia Annotations

```java
@Entity("users")
@Indexes({
    @Index(fields = @Field("name"), options = @IndexOptions(unique = true)),
    @Index(fields = @Field("coins"))
})
@Getter @Setter @NoArgsConstructor
public final class UserData implements PlayerPersistenceEntity {

    @Id private UUID uniqueId;

    @Property("username")
    private String name;

    private long coins;

    @Version
    private Long version; // optimistic locking
}
```

---

## Dirty Tracking

```java
user.setCoins(user.getCoins() + 100);
userRepo.markDirty(user);

userRepo.isDirty(uuid); // true

userRepo.save(user);    // save clears dirty flag
```

---

## Extending the Framework

### Custom Storage Provider

```java
public final class MyStorageProvider implements StorageProvider {
    @Override public CompletableFuture<Void> connect() { ... }
    @Override public CompletableFuture<Void> disconnect() { ... }
    @Override public boolean isConnected() { ... }
    @Override public String getProviderName() { return "MyStorage"; }
}
```

### Custom Cache Provider

```java
public final class RedisCacheProvider implements CacheProvider {
    @Override public <T> Optional<T> get(PersistenceKey key) { ... }
    @Override public void put(PersistenceKey key, Object entity) { ... }
    @Override public void invalidate(PersistenceKey key) { ... }
    @Override public boolean contains(PersistenceKey key) { ... }
    @Override public void invalidateAll() { ... }
    @Override public int size() { ... }
}
```

### Custom Settings Provider

```java
public final class DatabaseSettingsProvider implements SettingsProvider {

    @Override
    public PersistenceSettings provide() {
        return PersistenceSettings.builder()
            .cache(CacheSettings.builder()
                .maxSize(readFromDb("cache.maxSize", Long.class))
                .build())
            .build();
    }

    @Override
    public SettingsSource getSource() {
        return SettingsSource.PROGRAMMATIC;
    }
}
```

---

## API Reference

### DefaultPersistenceService Builder

| Method | Description |
|--------|-------------|
| `.storage(StorageProvider)` | Primary storage backend |
| `.cache(CacheProvider)` | Override cache (skips CacheSettings) |
| `.communication(CommunicationBus)` | Redis/NATS bus |
| `.sync(SyncService)` | Cross-server sync service |
| `.sector(SectorService)` | Sector management service |
| `.workerThreads(int)` | Thread pool size (default: CPU×2) |
| `.settings(PersistenceSettings)` | Full settings |
| `.settingsProvider(SettingsProvider)` | Settings provider (overrides `.settings()`) |
| `.build()` | Create `DefaultPersistenceService` |

### DefaultSyncService Builder

| Method | Description |
|--------|-------------|
| `.context(PersistenceContext)` | Required — provides bus, executor, settings |
| `.build()` | Create service |
| `.register()` | Start and subscribe to sync channels, returns `this` |

### DefaultSectorService Builder

| Method | Description |
|--------|-------------|
| `.context(PersistenceContext)` | Required |
| `.syncService(SyncService)` | Optional — enables data sync on transfer |
| `.build()` | Create service |
| `.register()` | Start and register packet handlers, returns `this` |
| `.buildConnectService(LastSectorProvider)` | Create a `DefaultSectorConnectService` for proxy-side routing |

### PaperPersistenceBootstrap

| Method | Description |
|--------|-------------|
| `new PaperPersistenceBootstrap(plugin, service)` | Create bootstrap |
| `.playerLoader(PlayerDataLoader)` | Optional — registers lifecycle listener |
| `.register()` | Start everything, returns `this` |
| `.unregister()` | Stop messaging and lifecycle |
| `.getMessagingBridge()` | `PaperPluginMessagingBridge` |
| `.getAnimationTask()` | `PaperLoadingAnimationTask` |

### VelocityPersistenceBootstrap

| Method | Description |
|--------|-------------|
| `new VelocityPersistenceBootstrap(plugin, proxy, service)` | Create bootstrap |
| `.sectorIntegration(boolean)` | Enable sector join routing; auto-registers listener when `proxyAutoConnectEnabled` |
| `.register()` | Start lifecycle, register event listener |
| `.unregister()` | Stop lifecycle |
| `.getSectorIntegration()` | `VelocitySectorIntegration` (null if not enabled) |
| `.getQueueService()` | `VelocityQueueService` |
| `.getPlayerTransferService()` | `VelocityPlayerTransferService` |
| `.getProxyBridge()` | `VelocityProxyBridge` |

### BungeePersistenceBootstrap

| Method | Description |
|--------|-------------|
| `new BungeePersistenceBootstrap(plugin, service)` | Create bootstrap |
| `.sectorIntegration(boolean)` | Enable sector join routing; auto-registers listener when `proxyAutoConnectEnabled` |
| `.register()` | Start lifecycle, register messaging channels |
| `.unregister()` | Stop messaging and lifecycle |
| `.getSectorIntegration()` | `BungeeSectorIntegration` (null if not enabled) |
| `.getQueueService()` | `BungeeQueueService` |
| `.getPlayerTransferService()` | `BungeePlayerTransferService` |

### AbstractPersistenceRepository

| Method | Description |
|--------|-------------|
| `.load(ID)` | Load from cache or storage |
| `.save(T)` | Save to storage and update cache |
| `.delete(ID)` | Delete from storage and evict cache |
| `.exists(ID)` | Check in cache then storage |
| `.findAll()` | All entities from storage |
| `.saveAll(List<T>)` | Batch save |
| `.getCached(ID)` | Cache only, `Optional<T>` |
| `.invalidateCache(ID)` | Evict from cache |
| `.markDirty(T)` | Flag as modified |
| `.isDirty(ID)` | Check dirty state |

### AbstractMongoRepository (additional)

| Method | Description |
|--------|-------------|
| `.findBy(Filter)` | Query with Morphia filter |
| `.findOneBy(Filter)` | First match |
| `.count()` | Document count |

### SettingsMerger

| Method | Description |
|--------|-------------|
| `SettingsMerger.builder()` | Fluent merge builder |
| `.withFile(Path)` | Add file layer |
| `.withEnvironment()` | Add env-var layer (highest priority) |
| `.withProvider(SettingsProvider)` | Add custom layer |
| `.build()` | Return merged `PersistenceSettings` |

---

## Architecture

```
persistence-api              ← interfaces, settings, results (no deps)
        ↑
persistence-core             ← default implementations, settings providers
        ↑               ↑
persistence-mongodb   persistence-plugin-messaging
                                    ↑
persistence-sync ←──────────────────┤
        ↑                           │
persistence-sector ─────────────────┘
        ↑
   ┌────┴──────────────────────────┐
persistence-paper  persistence-velocity  persistence-bungee

persistence-redis  ← standalone CommunicationBus
persistence-nats   ← standalone CommunicationBus
```

All modules are plain Java libraries. The user's plugin shades them and is the only artifact that needs to be deployed on the server.

---

## License

This project is licensed under the MIT License — see the [LICENSE](LICENSE) file for details.
