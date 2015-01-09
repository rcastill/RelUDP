package net;

import java.net.DatagramSocket;
import java.net.InetAddress;

public class P2PConnection extends Connection {
    protected P2PConnection(DatagramSocket socket, InetAddress addr, int port) {
        super(socket, addr, port);
    }

    @Override
    public void process(byte[] buffer) {

    }
}
