package pl.fejzu.persistence.nats.packet;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public final class NatsPacket {

    private final String subject;
    private final String type;
    private final byte[] payload;
    @Builder.Default
    private final long timestamp = System.currentTimeMillis();

    public static NatsPacket of(String subject, String type, byte[] payload) {
        return NatsPacket.builder()
            .subject(subject)
            .type(type)
            .payload(payload)
            .build();
    }
}
