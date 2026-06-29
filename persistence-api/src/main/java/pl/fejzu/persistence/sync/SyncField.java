package pl.fejzu.persistence.sync;

import lombok.Value;

@Value
public class SyncField {

    String name;
    SyncStrategy strategy;
    long delayMs;
    SyncScope scope;
}
