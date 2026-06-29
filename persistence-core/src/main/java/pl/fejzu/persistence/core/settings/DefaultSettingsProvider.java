package pl.fejzu.persistence.core.settings;

import pl.fejzu.persistence.settings.PersistenceSettings;
import pl.fejzu.persistence.settings.SettingsProvider;
import pl.fejzu.persistence.settings.SettingsSource;

public final class DefaultSettingsProvider implements SettingsProvider {

    @Override
    public PersistenceSettings provide() {
        return PersistenceSettings.defaults();
    }

    @Override
    public SettingsSource getSource() {
        return SettingsSource.DEFAULT;
    }
}
