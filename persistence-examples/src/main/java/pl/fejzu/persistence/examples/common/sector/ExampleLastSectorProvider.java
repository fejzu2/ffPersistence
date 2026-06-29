package pl.fejzu.persistence.examples.common.sector;

import pl.fejzu.persistence.examples.common.repository.ExampleUserRepository;
import pl.fejzu.persistence.sector.LastSectorProvider;
import pl.fejzu.persistence.sector.SectorId;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public final class ExampleLastSectorProvider implements LastSectorProvider {

    private final ExampleUserRepository userRepository;

    public ExampleLastSectorProvider(ExampleUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public CompletableFuture<Optional<SectorId>> getLastSector(UUID playerId) {
        return userRepository.load(playerId).thenApply(result -> {
            if (result.getData() == null) return Optional.empty();
            String lastSector = result.getData().getLastSector();
            if (lastSector == null || lastSector.isBlank()) return Optional.empty();
            return Optional.of(SectorId.of(lastSector));
        });
    }

    @Override
    public CompletableFuture<Void> saveLastSector(UUID playerId, SectorId sectorId) {
        return userRepository.setLastSector(playerId, sectorId.getValue());
    }
}
