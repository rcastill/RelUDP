package net;

import java.net.InetAddress;
import java.net.UnknownHostException;
import static net.NetworkUtilities.concatData;

public class ConnectionPacket extends ServerPacket {
    final Address address;

    public ConnectionPacket(int notf, int reqid, Connection conn) {
        super(notf, reqid);

        address = conn.getAddress();
        byte[] addrData = address.asData();
        data = concatData(data, addrData);
    }

    public ConnectionPacket(ServerPacket packet) throws UnknownHostException {
        super(packet);

        int intAddr = byteBuffer.getInt();
        int port = byteBuffer.getInt();

        String stringAddr = NetworkUtilities.integerToStringAddr(intAddr);
        address = new Address(InetAddress.getByName(stringAddr), port);
    }

    /**
     * Tells rather the packet indicates new connection
     * or a disconnection
     * @return <code>true</code> if connected, else <code>false</code>
     *
     */
    public boolean isConnectedNotf() {
        return proto == NetworkConstants.SERVNOTF_CLIENT_CONNECTED;
    }

    public Address getAddress() {
        return address;
    }
}
