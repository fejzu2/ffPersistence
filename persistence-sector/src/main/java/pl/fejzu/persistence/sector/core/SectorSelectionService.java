package pl.fejzu.persistence.sector.core;

import pl.fejzu.persistence.sector.LastSectorResolver;
import pl.fejzu.persistence.sector.Sector;
import pl.fejzu.persistence.sector.SectorConnectRequest;
import pl.fejzu.persistence.sector.SectorId;
import pl.fejzu.persistence.sector.SectorJoinResult;
import pl.fejzu.persistence.sector.SectorJoinStrategy;
import pl.fejzu.persistence.sector.SectorTransferFailReason;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public final class SectorSelectionService {

    private final LastSectorResolver lastSectorResolver;
    private final SectorAvailabilityService availabilityService;
    private final Random random = new Random();

    public SectorSelectionService(LastSectorResolver lastSectorResolver, SectorAvailabilityService availabilityService) {
        this.lastSectorResolver = lastSectorResolver;
        this.availabilityService = availabilityService;
    }

    public CompletableFuture<SectorJoinResult> selectSector(SectorConnectRequest request) {
        return switch (request.getStrategy()) {
            case LAST_SECTOR -> resolveLastSector(request);
            case FALLBACK_SECTOR -> resolveFallback(request);
            case BEST_AVAILABLE -> resolveBestAvailable(request);
            case LOWEST_ONLINE -> resolveLowestOnline(request);
            case RANDOM -> resolveRandom(request);
            case CUSTOM -> resolvePreferred(request);
        };
    }

    private CompletableFuture<SectorJoinResult> resolveLastSector(SectorConnectRequest request) {
        return lastSectorResolver.resolve(request.getPlayerId()).thenApply(opt ->
            opt.map(id -> SectorJoinResult.success(request.getPlayerId(), id, SectorJoinStrategy.LAST_SECTOR))
                .orElse(SectorJoinResult.failure(request.getPlayerId(), "No available sector", SectorTransferFailReason.NO_AVAILABLE_SECTOR))
        );
    }

    private CompletableFuture<SectorJoinResult> resolveFallback(SectorConnectRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            Optional<Sector> sector = availabilityService.findBestAvailable();
            return sector.map(s -> SectorJoinResult.success(request.getPlayerId(), s.getId(), SectorJoinStrategy.FALLBACK_SECTOR))
                .orElse(SectorJoinResult.failure(request.getPlayerId(), "No available sector", SectorTransferFailReason.NO_AVAILABLE_SECTOR));
        });
    }

    private CompletableFuture<SectorJoinResult> resolveBestAvailable(SectorConnectRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            Optional<Sector> sector = availabilityService.findBestAvailable();
            return sector.map(s -> SectorJoinResult.success(request.getPlayerId(), s.getId(), SectorJoinStrategy.BEST_AVAILABLE))
                .orElse(SectorJoinResult.failure(request.getPlayerId(), "No available sector", SectorTransferFailReason.NO_AVAILABLE_SECTOR));
        });
    }

    private CompletableFuture<SectorJoinResult> resolveLowestOnline(SectorConnectRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            Optional<Sector> sector = availabilityService.findLowestOnline();
            return sector.map(s -> SectorJoinResult.success(request.getPlayerId(), s.getId(), SectorJoinStrategy.LOWEST_ONLINE))
                .orElse(SectorJoinResult.failure(request.getPlayerId(), "No available sector", SectorTransferFailReason.NO_AVAILABLE_SECTOR));
        });
    }

    private CompletableFuture<SectorJoinResult> resolveRandom(SectorConnectRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            List<Sector> online = availabilityService.findBestAvailable()
                .map(s -> availabilityService.findLowestOnline().stream().collect(Collectors.toList()))
                .orElse(List.of());
            if (online.isEmpty()) {
                Optional<Sector> any = availabilityService.findBestAvailable();
                return any.map(s -> SectorJoinResult.success(request.getPlayerId(), s.getId(), SectorJoinStrategy.RANDOM))
                    .orElse(SectorJoinResult.failure(request.getPlayerId(), "No available sector", SectorTransferFailReason.NO_AVAILABLE_SECTOR));
            }
            Sector chosen = online.get(random.nextInt(online.size()));
            return SectorJoinResult.success(request.getPlayerId(), chosen.getId(), SectorJoinStrategy.RANDOM);
        });
    }

    private CompletableFuture<SectorJoinResult> resolvePreferred(SectorConnectRequest request) {
        SectorId preferred = request.getPreferredSector();
        if (preferred != null && availabilityService.isAvailable(preferred)) {
            return CompletableFuture.completedFuture(SectorJoinResult.success(request.getPlayerId(), preferred, SectorJoinStrategy.CUSTOM));
        }
        return resolveFallback(request);
    }
}
