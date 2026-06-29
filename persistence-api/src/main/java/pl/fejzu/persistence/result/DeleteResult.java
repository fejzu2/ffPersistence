package pl.fejzu.persistence.result;

public final class DeleteResult extends PersistenceResult {

    private DeleteResult(PersistenceStatus status, String message, Throwable cause) {
        super(status, message, cause);
    }

    public static DeleteResult success() {
        return new DeleteResult(PersistenceStatus.SUCCESS, null, null);
    }

    public static DeleteResult notFound() {
        return new DeleteResult(PersistenceStatus.NOT_FOUND, "Entity not found", null);
    }

    public static DeleteResult error(Throwable cause) {
        return new DeleteResult(PersistenceStatus.ERROR, cause.getMessage(), cause);
    }
}
