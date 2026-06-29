package pl.fejzu.persistence.redis;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class RedisChannel {

    public static final String PLAYER_LOAD = "ffpersistence:player:load";
    public static final String PLAYER_READY = "ffpersistence:player:ready";
    public static final String PLAYER_UNLOAD = "ffpersistence:player:unload";
    public static final String PLAYER_SAVE = "ffpersistence:player:save";
    public static final String BROADCAST = "ffpersistence:broadcast";
}
