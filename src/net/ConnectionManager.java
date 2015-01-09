package net;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.HashSet;

import static net.NetworkUtilities.addrToInteger;

/**
 * Collection of Connections
 */
public class ConnectionManager extends HashSet<Connection> {
    /**
     * Empty constructor
     */
    public ConnectionManager() {}

    /**
     * Sends packet to all connections
     * @param packet Packet to be sent
     */
    public void send(Packet packet) throws IOException {
        for (Connection conn : this)
            conn.send(packet);
    }

    /**
     * Gets <code>Connection</code> by <code>InetAddress</code>
     * @param addr Connection address (<code>InetAddress</code>)
     * @return Connection if exists, null otherwise
     */
    public Connection get(InetAddress addr) {
        for (Connection conn : this)
            if (conn.getInetAddress().equals(addr))
                return conn;

        return null;
    }

    public byte[] getAsData(Connection avoid) {
        ByteBuffer buff = ByteBuffer.allocate(Integer.BYTES * (1 + size() * 2));

        if (avoid == null)
            buff.putInt(size());

        else
            buff.putInt(size() - 1);

        for (Connection conn : this) {
            if (conn == avoid)
                continue;

            int addr = addrToInteger(conn.getAddress());
            buff.putInt(addr);
            buff.putInt(conn.getPort());
        }

        return buff.array();
    }

    public byte[] getAsData() {
        return getAsData(null);
    }

    public void doit(Instruction<Connection> ins) {
        for (Connection conn : this)
            ins.instruct(conn);
    }
}
