package pl.fejzu.persistence.sector;

import lombok.Value;

@Value
public class SectorPosition {

    double x;
    double y;
    double z;
    String world;

    public static SectorPosition of(double x, double y, double z, String world) {
        return new SectorPosition(x, y, z, world);
    }
}
