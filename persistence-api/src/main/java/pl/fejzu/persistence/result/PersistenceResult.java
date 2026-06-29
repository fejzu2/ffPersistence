package pl.fejzu.persistence.result;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public abstract class PersistenceResult {

    private final PersistenceStatus status;
    private final String message;
    private final Throwable cause;

    public boolean isSuccess() {
        return status == PersistenceStatus.SUCCESS || status == PersistenceStatus.CACHED;
    }

    public boolean isFailure() {
        return !isSuccess();
    }
}
