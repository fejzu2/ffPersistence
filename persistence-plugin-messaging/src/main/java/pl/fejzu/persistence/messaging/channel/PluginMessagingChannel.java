package pl.fejzu.persistence.messaging.channel;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class PluginMessagingChannel {

    public static final String MAIN = "ffpersistence:main";
    public static final String LOAD = "ffpersistence:load";
    public static final String READY = "ffpersistence:ready";
    public static final String SAVE = "ffpersistence:save";
    public static final String ERROR = "ffpersistence:error";
    public static final String SYNC = "ffpersistence:sync";
    public static final String SYNC_PLAYER = "ffpersistence:sync:player";
    public static final String SYNC_ENTITY = "ffpersistence:sync:entity";
    public static final String SECTOR = "ffpersistence:sector";
    public static final String SECTOR_TRANSFER = "ffpersistence:sector:transfer";
}
