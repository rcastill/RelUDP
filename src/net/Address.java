package net;

import java.net.InetAddress;
import java.nio.ByteBuffer;

public final class Address {
    public static final int BYTES = Integer.BYTES * 2;

    public final InetAddress addr;
    public final int port;

    public Address(InetAddress addr, int port) {
        this.addr = addr;
        this.port = port;
    }

    public byte[] asData() {
        ByteBuffer buff = ByteBuffer.allocate(Integer.BYTES * 2);
        int intAddr = NetworkUtilities.addrToInteger(addr);
        buff.putInt(intAddr);
        buff.putInt(port);
        return buff.array();
    }

    @Override
    public String toString() {
        return addr.toString() + ":" + port;
    }
}
