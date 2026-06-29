package pl.fejzu.persistence.redis.packet;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder
public final class RedisPacket {

    private final String type;
    private final String channel;
    private final String payload;
    @Builder.Default
    private final Map<String, String> headers = Map.of();
    @Builder.Default
    private final long timestamp = System.currentTimeMillis();

    public static RedisPacket of(String type, String channel, String payload) {
        return RedisPacket.builder()
            .type(type)
            .channel(channel)
            .payload(payload)
            .build();
    }
}
