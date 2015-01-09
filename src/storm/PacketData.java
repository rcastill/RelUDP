package storm;

import java.nio.ByteBuffer;

public interface PacketData {
    /**
     * Instructions to append data to the packet
     * @param byteBuffer Packet's <code>ByteBuffer</code>
     */
    public void append(ByteBuffer byteBuffer);

    /**
     * Populates <code>PacketData</code>
     * @param byteBuffer Buffer to get info
     */
    public void populate(ByteBuffer byteBuffer);

    /**
     * Gets size of packet data
     * @return BYTES
     */
    public int size();
}
