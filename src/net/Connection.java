package net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public abstract class Connection {
    private DatagramSocket socket;
    private Address address;

    protected Connection(DatagramSocket socket, InetAddress addr, int port) {
        this.socket = socket;
        address = new Address(addr, port);
    }

    protected Connection(DatagramSocket socket, Address address) {
        this.socket = socket;
        this.address = address;
    }

    public Address getAddress() {
        return address;
    }

    /**
     * Gets connection address
     * @return Address
     */
    public InetAddress getInetAddress() {
        return address.addr;
    }

    /**
     * Get connection port
     * @return Port
     */
    public int getPort() {
        return address.port;
    }

    /**
     * Sends packet to the other end
     * @param packet Packet to be sent
     */
    public void send(Packet packet) throws IOException {
        byte[] data = packet.getData();
        InetAddress addr = address.addr;
        int port = address.port;
        socket.send(new DatagramPacket(data, data.length, addr, port));
    }

    /**
     * Process received packet (as a byte array)
     */
    public abstract void process(byte[] buffer);
}
