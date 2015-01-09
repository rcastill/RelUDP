package storm;

import java.util.Collection;
import java.util.Random;

public class ServerPacket implements Packet {
    int reqid;
    byte[] data;

    public ServerPacket(byte[] data, Collection coll) {
        this.data = data;

        Random random = new Random();

        do reqid = random.nextInt();
        while (coll.contains(reqid));
    }

    @Override
    public byte[] getData() {
        return data;
    }
}
