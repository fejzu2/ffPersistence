package pl.fejzu.persistence.sector;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class SectorBounds {

    double minX;
    double minY;
    double minZ;
    double maxX;
    double maxY;
    double maxZ;
    String world;

    public boolean contains(SectorPosition pos) {
        if (!world.equals(pos.getWorld())) return false;
        return pos.getX() >= minX && pos.getX() <= maxX
            && pos.getY() >= minY && pos.getY() <= maxY
            && pos.getZ() >= minZ && pos.getZ() <= maxZ;
    }
}
