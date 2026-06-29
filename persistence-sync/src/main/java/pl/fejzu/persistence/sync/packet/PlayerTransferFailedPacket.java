package pl.fejzu.persistence.sync.packet;

import lombok.Builder;
import lombok.Getter;
import pl.fejzu.persistence.sync.SyncPacket;
import pl.fejzu.persistence.sync.SyncPacketType;
import pl.fejzu.persistence.sync.SyncReason;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Getter
@Builder
public final class PlayerTransferFailedPacket implements SyncPacket {

    @Builder.Default
    private final UUID packetId = UUID.randomUUID();
    @Builder.Default
    private final Instant createdAt = Instant.now();
    private final String sourceServer;
    private final String targetServer;
    private final UUID playerId;
    private final String targetSector;
    private final String failReason;
    @Builder.Default
    private final SyncReason reason = SyncReason.TRANSFER;
    @Builder.Default
    private final Map<String, Object> payload = new LinkedHashMap<>();

    @Override
    public SyncPacketType getType() {
        return SyncPacketType.PLAYER_TRANSFER_FAILED;
    }
}
