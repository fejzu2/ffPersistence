package pl.fejzu.persistence.sector;

public enum SectorJoinStrategy {
    LAST_SECTOR,
    FALLBACK_SECTOR,
    BEST_AVAILABLE,
    LOWEST_ONLINE,
    RANDOM,
    CUSTOM
}
