package pl.fejzu.persistence.sector.core;

import pl.fejzu.persistence.sector.SectorTransferHandler;
import pl.fejzu.persistence.sector.SectorTransferRequest;
import pl.fejzu.persistence.sector.SectorTransferResult;

import java.util.concurrent.CompletableFuture;

public final class SectorTransferCoordinator {

    private final SectorTransferHandler handler;

    public SectorTransferCoordinator(SectorTransferHandler handler) {
        this.handler = handler;
    }

    public CompletableFuture<SectorTransferResult> execute(SectorTransferRequest request) {
        return handler.handle(request);
    }
}
