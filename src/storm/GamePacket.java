package storm;

import java.nio.ByteBuffer;

/**
 * Standard packet sent between players
 */
public class GamePacket implements PacketData {
    float x, y;
    byte state, life;

    public GamePacket() {}

    public GamePacket(float x, float y, int state, int life) {
        this.x = x;
        this.y = y;
        this.state = (byte) state;
        this.life = (byte) life;
    }

    public GamePacket(float x, float y, StateByte stateByte, int life) {
        new GamePacket(x, y, stateByte.getByte(), life);
    }

    @Override
    public int size() {
        return Float.BYTES * 2 + Byte.BYTES * 2;
    }

    @Override
    public void append(ByteBuffer byteBuffer) {
        byteBuffer.putFloat(x);
        byteBuffer.putFloat(y);
        byteBuffer.put(state);
        byteBuffer.put(life);
    }

    @Override
    public void populate(ByteBuffer byteBuffer) {
        x = byteBuffer.getFloat();
        y = byteBuffer.getFloat();
        state = byteBuffer.get();
        life = byteBuffer.get();
    }
}
