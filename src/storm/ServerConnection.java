package storm;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ServerConnection extends Thread {
    FrontClient client;
    InetAddress addr;
    int port;

    BlockingQueue<ServerPacket> packets = new LinkedBlockingQueue<ServerPacket>();
    List<Integer> pendant = new ArrayList<Integer>();

    public ServerConnection(FrontClient client, InetAddress addr, int port) {
        super();
        this.client = client;
        this.addr = addr;
        this.port = port;
        setDaemon(true);
    }

    public ServerConnection(FrontClient client, String addr, int port) throws UnknownHostException {
        this(client, InetAddress.getByName(addr), port);
    }

    public void put(ServerPacket packet) {
        packets.add(packet);
    }

    @Override
    public void run() {
        while (client.isRunning()) try {
            ServerPacket packet = packets.take();

            if (pendant.contains(packet.reqid)) {
                byte[] data = packet.getData();
                client.send(new DatagramPacket(data, data.length, addr, port));
                packets.add(packet);
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}