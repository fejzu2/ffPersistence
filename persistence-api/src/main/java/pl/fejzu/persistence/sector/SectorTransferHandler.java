package pl.fejzu.persistence.sector;

import java.util.concurrent.CompletableFuture;

public interface SectorTransferHandler {

    CompletableFuture<SectorTransferResult> handle(SectorTransferRequest request);
}
