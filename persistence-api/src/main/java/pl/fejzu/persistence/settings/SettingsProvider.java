package pl.fejzu.persistence.settings;

public interface SettingsProvider {

    PersistenceSettings provide();

    SettingsSource getSource();

    default boolean isAvailable() {
        return true;
    }
}
