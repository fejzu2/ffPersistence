package pl.fejzu.persistence.result;

public final class SaveResult extends PersistenceResult {

    private SaveResult(PersistenceStatus status, String message, Throwable cause) {
        super(status, message, cause);
    }

    public static SaveResult success() {
        return new SaveResult(PersistenceStatus.SUCCESS, null, null);
    }

    public static SaveResult error(Throwable cause) {
        return new SaveResult(PersistenceStatus.ERROR, cause.getMessage(), cause);
    }

    public static SaveResult error(String message) {
        return new SaveResult(PersistenceStatus.ERROR, message, null);
    }
}
