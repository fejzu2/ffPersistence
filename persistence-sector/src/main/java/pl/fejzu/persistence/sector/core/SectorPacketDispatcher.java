package pl.fejzu.persistence.sector.core;

import pl.fejzu.persistence.communication.CommunicationBus;
import pl.fejzu.persistence.communication.CommunicationChannel;
import pl.fejzu.persistence.messaging.PluginMessagingMessage;
import pl.fejzu.persistence.messaging.packet.PlayerTransferReadyPacket;
import pl.fejzu.persistence.messaging.packet.PlayerTransferStartPacket;
import pl.fejzu.persistence.sector.SectorId;
import pl.fejzu.persistence.sector.SectorTransferRequest;

import java.util.UUID;

public final class SectorPacketDispatcher {

    private static final String CHANNEL_SECTOR_TRANSFER = "ffpersistence:sector:transfer";
    private static final String CHANNEL_SECTOR = "ffpersistence:sector";

    private final CommunicationBus bus;
    private final SectorPlayerDataPreloader preloader;
    private final String serverName;

    public SectorPacketDispatcher(CommunicationBus bus, SectorPlayerDataPreloader preloader, String serverName) {
        this.bus = bus;
        this.preloader = preloader;
        this.serverName = serverName;
    }

    public void registerHandlers() {
        if (bus == null || !bus.isConnected()) return;
        bus.subscribe(CommunicationChannel.of(CHANNEL_SECTOR_TRANSFER), message -> {
            if ("PLAYER_TRANSFER_READY".equals(message.getType())) {
                PlayerTransferReadyPacket packet = PlayerTransferReadyPacket.deserialize(message.getPayload());
                preloader.notifyReady(packet.getPlayerId());
            }
        });
    }

    public void sendTransferStart(SectorTransferRequest request) {
        if (bus == null || !bus.isConnected()) return;
        String source = request.getSourceSector() != null ? request.getSourceSector().getValue() : "";
        String target = request.getTargetSector() != null ? request.getTargetSector().getValue() : "";
        PlayerTransferStartPacket packet = new PlayerTransferStartPacket(
            request.getPlayerId(),
            request.getPlayerName() != null ? request.getPlayerName() : "",
            source,
            target,
            target
        );
        bus.publish(CommunicationChannel.of(CHANNEL_SECTOR_TRANSFER), PluginMessagingMessage.of("PLAYER_TRANSFER_START", packet.serialize()));
    }

    public void sendTransferReady(UUID playerId, SectorId targetSector) {
        if (bus == null || !bus.isConnected()) return;
        PlayerTransferReadyPacket packet = new PlayerTransferReadyPacket(
            playerId,
            targetSector != null ? targetSector.getValue() : "",
            serverName
        );
        bus.publish(CommunicationChannel.of(CHANNEL_SECTOR_TRANSFER), PluginMessagingMessage.of("PLAYER_TRANSFER_READY", packet.serialize()));
    }
}
