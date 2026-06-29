package pl.fejzu.persistence.sector;

import lombok.Value;

@Value
public class SectorId {

    String value;

    public static SectorId of(String value) {
        return new SectorId(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
