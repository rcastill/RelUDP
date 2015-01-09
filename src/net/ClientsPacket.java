package net;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import static net.NetworkUtilities.integerToAddr;

public class ClientsPacket extends ServerPacket {
    Address[] addresses;

    public ClientsPacket(ServerPacket packet) throws UnknownHostException {
        super(packet);

        int size = byteBuffer.getInt();
        addresses = new Address[size];

        System.out.println("size = " + size);

        int intAddr, port;

        for (int i = 0; i < size; i++) {
            intAddr = byteBuffer.getInt();
            port = byteBuffer.getInt();
            InetAddress addr = integerToAddr(intAddr);
            addresses[i] = new Address(addr, port);
        }
    }

    public Address[] getAdresses() {
        return addresses;
    }

    @Override
    public String toString() {
        String string = (addresses.length != 0) ? "" : "none";

        for (Address addr : addresses)
            string += addr.toString() + "\n";

        return string;
    }
}
