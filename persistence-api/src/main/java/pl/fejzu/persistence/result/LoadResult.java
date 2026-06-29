package pl.fejzu.persistence.result;

import lombok.Getter;

import java.util.Optional;

@Getter
public final class LoadResult<T> extends PersistenceResult {

    private final T data;
    private final DataState dataState;

    private LoadResult(PersistenceStatus status, String message, Throwable cause, T data, DataState dataState) {
        super(status, message, cause);
        this.data = data;
        this.dataState = dataState;
    }

    public static <T> LoadResult<T> success(T data, DataState dataState) {
        return new LoadResult<>(PersistenceStatus.SUCCESS, null, null, data, dataState);
    }

    public static <T> LoadResult<T> cached(T data) {
        return new LoadResult<>(PersistenceStatus.CACHED, null, null, data, DataState.CACHED);
    }

    public static <T> LoadResult<T> notFound() {
        return new LoadResult<>(PersistenceStatus.NOT_FOUND, "Entity not found", null, null, DataState.NOT_FOUND);
    }

    public static <T> LoadResult<T> error(Throwable cause) {
        return new LoadResult<>(PersistenceStatus.ERROR, cause.getMessage(), cause, null, DataState.ERROR);
    }

    public Optional<T> asOptional() {
        return Optional.ofNullable(data);
    }
}
