package net;

import java.util.List;

import static net.NetworkConstants.*;

public class ServerPacketInterpreter {
    private final List<Packet> packets;

    public ServerPacketInterpreter(List<Packet> packets) {
        this.packets = packets;
    }

    private Packet getByProto(int proto) {
        for (Packet packet : packets) {
            ServerPacket spacket = (ServerPacket) packet;

            if (spacket.proto == proto) {
                packets.remove(packet);
                return packet;
            }
        }

        return null;
    }

    public ConnectionPacket getConnectionNotification() {
        Packet packet = getByProto(SERVNOTF_CLIENT_CONNECTED);
        return (packet == null) ? null : (ConnectionPacket) packet;
    }

    public ConnectionPacket getDisconnectionNotification() {
        Packet packet = getByProto(SERVNOTF_CLIENT_DISCONNECTED);
        return (packet == null) ? null : (ConnectionPacket) packet;
    }

    public ClientsPacket getClientsPacket () {
        Packet packet = getByProto(SERVREQ_GET_CLIENTS);
        return (packet == null) ? null : (ClientsPacket) packet;
    }
}
