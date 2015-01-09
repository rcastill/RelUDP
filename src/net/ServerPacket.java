package net;

import java.nio.ByteBuffer;

import static net.NetworkConstants.*;
import static net.NetworkUtilities.getProtocol;

public class ServerPacket extends Packet {
    public static final int HEADER_SIZE = PROTOCOL_SIZE + REQID_SIZE;

    public final int proto, reqid;

    public ServerPacket(byte[] data) {
        super(data);

        byteBuffer = ByteBuffer.wrap(data);

        proto = byteBuffer.getInt();
        reqid = byteBuffer.getInt();
    }

    public ServerPacket(int proto, int reqid) {
        this.proto = proto;
        this.reqid = reqid;

        byteBuffer = ByteBuffer.allocate(HEADER_SIZE);
        byteBuffer.putInt(proto);
        byteBuffer.putInt(reqid);

        data = byteBuffer.array();
    }

    public ServerPacket(ServerPacket other) {
        super(other);
        proto = other.proto;
        reqid = other.reqid;
    }
}
