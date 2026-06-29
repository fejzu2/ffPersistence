package pl.fejzu.persistence.nats;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class NatsSubject {

    public static final String PLAYER_LOAD = "ffpersistence.player.load";
    public static final String PLAYER_READY = "ffpersistence.player.ready";
    public static final String PLAYER_UNLOAD = "ffpersistence.player.unload";
    public static final String PLAYER_SAVE = "ffpersistence.player.save";
    public static final String BROADCAST = "ffpersistence.broadcast";

    public static String custom(String suffix) {
        return "ffpersistence." + suffix;
    }
}
