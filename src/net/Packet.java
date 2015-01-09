package net;

import java.nio.ByteBuffer;

public class Packet {
    protected byte[] data;
    protected ByteBuffer byteBuffer;

    public Packet(byte[] data) {
        this.data = data;
    }

    public Packet(Packet other) {
        data = other.data;
        byteBuffer = other.byteBuffer;
    }

    public Packet() {}

    /**
     * Gets packet as byte array
     * @return Data
     */
    public byte[] getData() {
        return data;
    }

    public boolean compare(Packet other) {
        if (data.length != other.data.length)
            return false;

        for (int i = 0; i < data.length; i++)
            if (data[i] != other.data[i])
                return false;

        return true;
    }
}
