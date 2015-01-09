package storm;

import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class BackClientManager {
    List<BackClient> clients = new ArrayList<BackClient>();
    final int max;

    public BackClientManager(int max) {
        this.max = max;
    }

    public BackClient getClientByAddress(InetAddress address) {
        for (BackClient client : clients)
            if (client.addr == address)
                return client;

        return null;
    }

    private byte[] getClientsAsData(ByteBuffer buffer) {
        for (BackClient client : clients) {
            int intAddr = NetworkUtilities.ipToInteger(client.addr);
            buffer.putInt(intAddr);
        }

        return buffer.array();
    }

    public byte[] getClientsAsData(int proto, int reqid) {
        ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES * (clients.size() + 2));
        buffer.putInt(proto);
        buffer.putInt(reqid);
        return getClientsAsData(buffer);
    }

    public byte[] getClientsAsData() {
        ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES * clients.size());
        return getClientsAsData(buffer);
    }

    public void add(BackClient client) {
        clients.add(client);
    }
}
