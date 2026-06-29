package pl.fejzu.persistence.core.settings;

import pl.fejzu.persistence.settings.PersistenceSettings;
import pl.fejzu.persistence.settings.SettingsProvider;
import pl.fejzu.persistence.settings.SettingsSource;

import java.util.Arrays;
import java.util.List;

public final class SettingsMerger {

    private static final PersistenceSettings DEFAULTS = PersistenceSettings.defaults();

    /**
     * Merges settings from ordered providers. Later providers override earlier ones
     * at the sub-settings block level — a sub-settings block from a later provider
     * takes precedence only when it differs from the default block.
     */
    public PersistenceSettings merge(List<SettingsProvider> providers) {
        PersistenceSettings result = DEFAULTS;
        for (SettingsProvider provider : providers) {
            if (!provider.isAvailable()) continue;
            PersistenceSettings override = provider.provide();
            result = applyOverride(result, override);
        }
        return result.withSource(SettingsSource.MERGED);
    }

    public PersistenceSettings merge(SettingsProvider... providers) {
        return merge(Arrays.asList(providers));
    }

    /**
     * Convenience builder for constructing a merged provider chain.
     */
    public static Builder builder() {
        return new Builder();
    }

    private PersistenceSettings applyOverride(PersistenceSettings base, PersistenceSettings override) {
        PersistenceSettings result = base;

        if (!override.getLoading().equals(DEFAULTS.getLoading())) {
            result = result.withLoading(override.getLoading());
        }
        if (!override.getCache().equals(DEFAULTS.getCache())) {
            result = result.withCache(override.getCache());
        }
        if (!override.getStorage().equals(DEFAULTS.getStorage())) {
            result = result.withStorage(override.getStorage());
        }
        if (!override.getCommunication().equals(DEFAULTS.getCommunication())) {
            result = result.withCommunication(override.getCommunication());
        }
        if (!override.getPlayerLifecycle().equals(DEFAULTS.getPlayerLifecycle())) {
            result = result.withPlayerLifecycle(override.getPlayerLifecycle());
        }
        if (!override.getProxy().equals(DEFAULTS.getProxy())) {
            result = result.withProxy(override.getProxy());
        }
        if (!override.getQueue().equals(DEFAULTS.getQueue())) {
            result = result.withQueue(override.getQueue());
        }
        if (!override.getSync().equals(DEFAULTS.getSync())) {
            result = result.withSync(override.getSync());
        }
        if (!override.getSector().equals(DEFAULTS.getSector())) {
            result = result.withSector(override.getSector());
        }

        return result;
    }

    public static final class Builder {

        private SettingsProvider base = new DefaultSettingsProvider();

        public Builder base(SettingsProvider base) {
            this.base = base;
            return this;
        }

        public Builder withFile(java.nio.file.Path configFile) {
            this.base = new FileSettingsProvider(configFile, this.base);
            return this;
        }

        public Builder withEnvironment() {
            this.base = new EnvironmentSettingsProvider(this.base);
            return this;
        }

        public Builder withProvider(SettingsProvider provider) {
            SettingsProvider current = this.base;
            this.base = new SettingsProvider() {
                @Override
                public PersistenceSettings provide() {
                    PersistenceSettings baseSettings = current.provide();
                    PersistenceSettings override = provider.provide();
                    return new SettingsMerger().applyOverride(baseSettings, override);
                }

                @Override
                public SettingsSource getSource() {
                    return SettingsSource.MERGED;
                }
            };
            return this;
        }

        public PersistenceSettings build() {
            return base.provide().withSource(SettingsSource.MERGED);
        }
    }
}
