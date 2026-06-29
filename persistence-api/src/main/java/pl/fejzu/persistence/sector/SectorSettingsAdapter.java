package pl.fejzu.persistence.sector;

import pl.fejzu.persistence.settings.SectorSettings;

public interface SectorSettingsAdapter {

    SectorSettings getSettings();

    long getTransferTimeoutMs();

    boolean shouldPreload();

    boolean shouldLockData();

    boolean shouldFullSyncOnTransfer();

    boolean shouldUnloadSourceCache();
}
