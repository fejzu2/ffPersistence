package pl.fejzu.persistence.settings;

public enum ConflictStrategy {
    LAST_WRITE_WINS,
    FIRST_WRITE_WINS,
    REJECT,
    MERGE
}
