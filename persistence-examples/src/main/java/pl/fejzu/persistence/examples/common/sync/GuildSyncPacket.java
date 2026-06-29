package pl.fejzu.persistence.examples.common.sync;

import lombok.Builder;
import lombok.Getter;
import pl.fejzu.persistence.sync.SyncPacket;
import pl.fejzu.persistence.sync.SyncPacketType;
import pl.fejzu.persistence.sync.SyncReason;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Getter
@Builder
public final class GuildSyncPacket implements SyncPacket {

    @Builder.Default
    private final UUID packetId = UUID.randomUUID();
    @Builder.Default
    private final Instant createdAt = Instant.now();
    private final String sourceServer;
    private final String targetServer;
    private final UUID playerId;
    private final String guildId;
    private final String guildName;
    private final long guildCoins;
    @Builder.Default
    private final SyncReason reason = SyncReason.MANUAL;

    @Override
    public SyncPacketType getType() {
        return SyncPacketType.CUSTOM;
    }

    @Override
    public Map<String, Object> getPayload() {
        return Map.of("guildId", guildId, "guildName", guildName, "guildCoins", guildCoins);
    }
}
