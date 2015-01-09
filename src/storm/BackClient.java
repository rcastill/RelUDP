package storm;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class BackClient extends Thread {
    Server server;
    InetAddress addr;
    int port;

    BlockingQueue<ServerPacket> sendQueue = new LinkedBlockingQueue<ServerPacket>();
    List<Integer> pendant = new ArrayList<Integer>();

    public BackClient(Server server, InetAddress addr, int port) {
        this.server = server;
        this.addr = addr;
        this.port = port;
    }

    private int generateRequestId() {
        Random random = new Random();
        int reqid;

        do reqid = random.nextInt();
        while (pendant.contains(reqid));

        return reqid;
    }

    public void process(int proto) throws IOException {
        byte[] data;
        byte[] ans;

        int reqid;

        switch (proto) {
            case NetworkConstants.SERVREQ_CONNECTED_CLIENTS:
                reqid = generateRequestId();
                ans = server.clients.getClientsAsData(proto, reqid);
                //sendQueue.add(new ServerRequest(reqid, ans));
                break;

            case NetworkConstants.SERVER_ACKNOWLEDGEMENT:
                data = new byte[NetworkConstants.GetPacketSize(proto)];
                System.arraycopy(server.buffer, 4, data, 0, data.length);
                reqid = ByteBuffer.wrap(data).getInt();

                if (pendant.contains(reqid))
                    pendant.remove(new Integer(reqid));

                else
                    System.out.println("ACK Dropped (" + reqid + ")");

                break;

            case NetworkConstants.SERVREQ_DISCONNECT:
                break;

            case NetworkConstants.SERVREQ_TEST:
                data = new byte[NetworkConstants.GetPacketSize(proto)];
                System.arraycopy(server.buffer, 4, data, 0, data.length);
                ByteBuffer buffer = ByteBuffer.wrap(data);
                System.out.println("Test packet received:");
                System.out.println(buffer.getInt());
                System.out.println(buffer.getInt());
                break;

            default:
                System.out.println("Unknown protocol (" + proto + "). Dropped.");
                break;
        }
    }

    @Override
    public void run() {
        while (server.isRunning()) try {
            ServerPacket ans = sendQueue.take();

            if (pendant.contains(ans.reqid)) {
                DatagramPacket packet = new DatagramPacket(ans.data, ans.data.length, addr, port);
                server.send(packet);
                sendQueue.add(ans);
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Disconnection");
        }
    }
}
