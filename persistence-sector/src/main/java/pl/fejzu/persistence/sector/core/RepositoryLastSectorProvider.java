package pl.fejzu.persistence.sector.core;

import pl.fejzu.persistence.sector.LastSectorProvider;
import pl.fejzu.persistence.sector.SectorId;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Function;

public final class RepositoryLastSectorProvider implements LastSectorProvider {

    private final Function<UUID, CompletableFuture<Optional<SectorId>>> loader;
    private final BiFunction<UUID, SectorId, CompletableFuture<Void>> saver;

    public RepositoryLastSectorProvider(
        Function<UUID, CompletableFuture<Optional<SectorId>>> loader,
        BiFunction<UUID, SectorId, CompletableFuture<Void>> saver
    ) {
        this.loader = loader;
        this.saver = saver;
    }

    @Override
    public CompletableFuture<Optional<SectorId>> getLastSector(UUID playerId) {
        return loader.apply(playerId);
    }

    @Override
    public CompletableFuture<Void> saveLastSector(UUID playerId, SectorId sectorId) {
        return saver.apply(playerId, sectorId);
    }
}
