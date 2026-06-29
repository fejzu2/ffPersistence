package pl.fejzu.persistence.sync;

import lombok.Value;

@Value
public class SyncChannel {

    String name;

    public static SyncChannel of(String name) {
        return new SyncChannel(name);
    }
}
