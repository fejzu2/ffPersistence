package pl.fejzu.persistence.sync;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public interface SyncPacket {

    UUID getPacketId();

    Instant getCreatedAt();

    String getSourceServer();

    String getTargetServer();

    UUID getPlayerId();

    SyncReason getReason();

    SyncPacketType getType();

    Map<String, Object> getPayload();
}
