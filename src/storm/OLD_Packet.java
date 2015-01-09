package storm;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.nio.ByteBuffer;

public class OLD_Packet {
    //public static final int BYTES = Integer.BYTES * 2 + Acknowledgement.BYTES; 1.8
    public static final int BYTES = 4 * 2 + Acknowledgement.BYTES;

    public final int proto, seq, ack, ackb, size;


    private ByteBuffer buffer;
    PacketData data;

    public OLD_Packet(int proto, int seq, Acknowledgement ack, PacketData data) {
        this.proto = proto;
        this.seq = seq;
        this.ack = ack.getAck();
        ackb = ack.getAckBitfield();
        this.data = data;

        size = BYTES + data.size();
        buffer = ByteBuffer.allocate(size);

        buffer.putInt(proto);
        buffer.putInt(seq);
        buffer.putInt(ack.getAck());
        buffer.putInt(ack.getAckBitfield());
        data.append(buffer);
    }

    public OLD_Packet(DatagramPacket datagramPacket, PacketData packetData) {
        byte[] data = datagramPacket.getData();
        buffer = ByteBuffer.wrap(data);

        proto = buffer.getInt();
        seq = buffer.getInt();
        ack = buffer.getInt();
        ackb = buffer.getInt();

        packetData.populate(buffer);
        this.data = packetData;

        size = BYTES + packetData.size();
    }

    public ByteBuffer getBuffer() {
        return buffer;
    }

    public PacketData getData() {
        return data;
    }

    public DatagramPacket getDatagramPacket(InetAddress addr, int port) {
        byte[] arr = buffer.array();
        return new DatagramPacket(arr, arr.length, addr, port);
    }
}
