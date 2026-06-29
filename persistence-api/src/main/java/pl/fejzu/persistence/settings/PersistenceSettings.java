package pl.fejzu.persistence.settings;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Builder(toBuilder = true)
@Getter
@EqualsAndHashCode
public final class PersistenceSettings {

    @Builder.Default
    private final LoadingSettings loading = LoadingSettings.defaults();

    @Builder.Default
    private final CacheSettings cache = CacheSettings.defaults();

    @Builder.Default
    private final StorageSettings storage = StorageSettings.defaults();

    @Builder.Default
    private final CommunicationSettings communication = CommunicationSettings.defaults();

    @Builder.Default
    private final PlayerLifecycleSettings playerLifecycle = PlayerLifecycleSettings.defaults();

    @Builder.Default
    private final ProxySettings proxy = ProxySettings.defaults();

    @Builder.Default
    private final QueueSettings queue = QueueSettings.defaults();

    @Builder.Default
    private final SyncSettings sync = SyncSettings.defaults();

    @Builder.Default
    private final SectorSettings sector = SectorSettings.defaults();

    @Builder.Default
    private final SettingsSource source = SettingsSource.DEFAULT;

    public static PersistenceSettings defaults() {
        return builder().build();
    }

    public PersistenceSettings withLoading(LoadingSettings loading) {
        return toBuilder().loading(loading).build();
    }

    public PersistenceSettings withCache(CacheSettings cache) {
        return toBuilder().cache(cache).build();
    }

    public PersistenceSettings withStorage(StorageSettings storage) {
        return toBuilder().storage(storage).build();
    }

    public PersistenceSettings withCommunication(CommunicationSettings communication) {
        return toBuilder().communication(communication).build();
    }

    public PersistenceSettings withPlayerLifecycle(PlayerLifecycleSettings playerLifecycle) {
        return toBuilder().playerLifecycle(playerLifecycle).build();
    }

    public PersistenceSettings withProxy(ProxySettings proxy) {
        return toBuilder().proxy(proxy).build();
    }

    public PersistenceSettings withQueue(QueueSettings queue) {
        return toBuilder().queue(queue).build();
    }

    public PersistenceSettings withSync(SyncSettings sync) {
        return toBuilder().sync(sync).build();
    }

    public PersistenceSettings withSector(SectorSettings sector) {
        return toBuilder().sector(sector).build();
    }

    public PersistenceSettings withSource(SettingsSource source) {
        return toBuilder().source(source).build();
    }
}
