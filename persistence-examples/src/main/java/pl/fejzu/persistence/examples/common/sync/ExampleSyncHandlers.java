package pl.fejzu.persistence.examples.common.sync;

import pl.fejzu.persistence.examples.common.factory.ExampleLogger;
import pl.fejzu.persistence.examples.common.repository.ExampleGuildRepository;
import pl.fejzu.persistence.examples.common.repository.ExampleUserRepository;
import pl.fejzu.persistence.sync.SyncContext;
import pl.fejzu.persistence.sync.SyncPacketCodec;
import pl.fejzu.persistence.sync.packet.PayloadSerializer;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public final class ExampleSyncHandlers {

    private static final ExampleLogger LOG = ExampleLogger.of(ExampleSyncHandlers.class);

    private ExampleSyncHandlers() {}

    public static void handleCombatTag(CombatTagSyncPacket packet, SyncContext context) {
        UUID playerId = packet.getPlayerId();
        if (packet.isTagged()) {
            LOG.info("Player " + playerId + " is combat-tagged until " + Instant.ofEpochMilli(packet.getExpiresAt()));
        } else {
            LOG.info("Player " + playerId + " combat tag cleared");
        }
    }

    public static void handleCoinsUpdate(CoinsUpdatePacket packet, SyncContext context) {
        ExampleUserRepository repo = context.getPersistenceContext()
            .getRegistry()
            .get(ExampleUserRepository.class);
        repo.getCached(packet.getPlayerId()).ifPresent(user -> {
            user.setCoins(packet.getNewBalance());
            LOG.info("Synced coins for " + packet.getPlayerId() + " -> " + packet.getNewBalance());
        });
    }

    public static void handleGuildSync(GuildSyncPacket packet, SyncContext context) {
        ExampleGuildRepository repo = context.getPersistenceContext()
            .getRegistry()
            .get(ExampleGuildRepository.class);
        repo.getCached(packet.getGuildId()).ifPresent(guild -> {
            guild.setName(packet.getGuildName());
            guild.setCoins(packet.getGuildCoins());
            LOG.info("Synced guild " + packet.getGuildId() + " -> " + packet.getGuildName());
        });
    }

    public static final class CombatTagCodec implements SyncPacketCodec<CombatTagSyncPacket> {

        @Override
        public byte[] encode(CombatTagSyncPacket packet) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("packetId", packet.getPacketId());
            map.put("createdAt", packet.getCreatedAt().toEpochMilli());
            map.put("sourceServer", packet.getSourceServer());
            map.put("targetServer", packet.getTargetServer());
            map.put("playerId", packet.getPlayerId());
            map.put("reason", packet.getReason().name());
            map.put("tagged", packet.isTagged());
            map.put("expiresAt", packet.getExpiresAt());
            return PayloadSerializer.serialize(map);
        }

        @Override
        public CombatTagSyncPacket decode(byte[] data) {
            Map<String, Object> map = PayloadSerializer.deserialize(data);
            return CombatTagSyncPacket.builder()
                .packetId((UUID) map.get("packetId"))
                .createdAt(Instant.ofEpochMilli((Long) map.get("createdAt")))
                .sourceServer((String) map.get("sourceServer"))
                .targetServer((String) map.get("targetServer"))
                .playerId((UUID) map.get("playerId"))
                .reason(pl.fejzu.persistence.sync.SyncReason.valueOf((String) map.get("reason")))
                .tagged((Boolean) map.get("tagged"))
                .expiresAt((Long) map.get("expiresAt"))
                .build();
        }

        @Override
        public String getTypeId() {
            return "combat-tag";
        }
    }

    public static final class CoinsUpdateCodec implements SyncPacketCodec<CoinsUpdatePacket> {

        @Override
        public byte[] encode(CoinsUpdatePacket packet) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("packetId", packet.getPacketId());
            map.put("createdAt", packet.getCreatedAt().toEpochMilli());
            map.put("sourceServer", packet.getSourceServer());
            map.put("targetServer", packet.getTargetServer());
            map.put("playerId", packet.getPlayerId());
            map.put("reason", packet.getReason().name());
            map.put("newBalance", packet.getNewBalance());
            map.put("delta", packet.getDelta());
            return PayloadSerializer.serialize(map);
        }

        @Override
        public CoinsUpdatePacket decode(byte[] data) {
            Map<String, Object> map = PayloadSerializer.deserialize(data);
            return CoinsUpdatePacket.builder()
                .packetId((UUID) map.get("packetId"))
                .createdAt(Instant.ofEpochMilli((Long) map.get("createdAt")))
                .sourceServer((String) map.get("sourceServer"))
                .targetServer((String) map.get("targetServer"))
                .playerId((UUID) map.get("playerId"))
                .reason(pl.fejzu.persistence.sync.SyncReason.valueOf((String) map.get("reason")))
                .newBalance((Long) map.get("newBalance"))
                .delta((Long) map.get("delta"))
                .build();
        }

        @Override
        public String getTypeId() {
            return "coins-update";
        }
    }

    public static final class GuildSyncCodec implements SyncPacketCodec<GuildSyncPacket> {

        @Override
        public byte[] encode(GuildSyncPacket packet) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("packetId", packet.getPacketId());
            map.put("createdAt", packet.getCreatedAt().toEpochMilli());
            map.put("sourceServer", packet.getSourceServer());
            map.put("targetServer", packet.getTargetServer());
            map.put("playerId", packet.getPlayerId() != null ? packet.getPlayerId() : UUID.fromString("00000000-0000-0000-0000-000000000000"));
            map.put("reason", packet.getReason().name());
            map.put("guildId", packet.getGuildId());
            map.put("guildName", packet.getGuildName());
            map.put("guildCoins", packet.getGuildCoins());
            return PayloadSerializer.serialize(map);
        }

        @Override
        public GuildSyncPacket decode(byte[] data) {
            Map<String, Object> map = PayloadSerializer.deserialize(data);
            UUID playerId = (UUID) map.get("playerId");
            UUID nilUuid = UUID.fromString("00000000-0000-0000-0000-000000000000");
            return GuildSyncPacket.builder()
                .packetId((UUID) map.get("packetId"))
                .createdAt(Instant.ofEpochMilli((Long) map.get("createdAt")))
                .sourceServer((String) map.get("sourceServer"))
                .targetServer((String) map.get("targetServer"))
                .playerId(nilUuid.equals(playerId) ? null : playerId)
                .reason(pl.fejzu.persistence.sync.SyncReason.valueOf((String) map.get("reason")))
                .guildId((String) map.get("guildId"))
                .guildName((String) map.get("guildName"))
                .guildCoins((Long) map.get("guildCoins"))
                .build();
        }

        @Override
        public String getTypeId() {
            return "guild-sync";
        }
    }
}
