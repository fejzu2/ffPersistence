package pl.fejzu.persistence.sector;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface LastSectorResolver {

    CompletableFuture<Optional<SectorId>> resolve(UUID playerId);
}
