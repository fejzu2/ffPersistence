package pl.fejzu.persistence.sector;

import java.util.concurrent.CompletableFuture;

public interface SectorTransfer {

    SectorTransfer to(SectorId targetSector);

    SectorTransfer reason(SectorTransferReason reason);

    SectorTransfer preload(boolean preload);

    SectorTransfer lockData(boolean lockData);

    SectorTransfer syncFullPlayer(boolean syncFullPlayer);

    SectorTransfer playerName(String playerName);

    CompletableFuture<SectorTransferResult> execute();
}
