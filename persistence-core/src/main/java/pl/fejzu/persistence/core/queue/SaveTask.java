package pl.fejzu.persistence.core.queue;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pl.fejzu.persistence.entity.PersistenceEntity;
import pl.fejzu.persistence.repository.PersistenceRepository;
import pl.fejzu.persistence.result.SaveReason;

@Getter
@RequiredArgsConstructor
public final class SaveTask<T extends PersistenceEntity<?>> {

    private final T entity;
    private final PersistenceRepository<T, ?> repository;
    private final SaveReason reason;

    @SuppressWarnings("unchecked")
    public void execute() {
        repository.save(entity).join();
    }
}
