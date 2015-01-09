package net;

import java.io.IOException;
import java.net.InetAddress;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static net.NetworkConstants.*;
import static net.NetworkUtilities.concatData;

public class BackClient extends Connection implements Runnable {
    Server server;
    boolean running = true;

    Set<Integer> pendant = new HashSet<Integer>();
    BlockingQueue<Packet> packets = new LinkedBlockingQueue<Packet>();

    public final Thread thread;

    public BackClient(Server server, InetAddress addr, int port) {
        super(server, addr, port);
        this.server = server;
        thread = new Thread(this);
        thread.setDaemon(true);

        System.out.println("Client connected (" + getAddress().toString() + ")");
    }

    public int generateRequestId() {
        Random random = new Random();
        int reqid;

        do reqid = random.nextInt();
        while (!pendant.add(reqid));

        return reqid;
    }

    @Override
    public void process(byte[] buffer) {
        // Isolate header
        byte[] header = new byte[PROTOCOL_SIZE + REQID_SIZE];
        System.arraycopy(buffer, 0, header, 0, header.length);

        // Get packet header
        ServerPacket req = new ServerPacket(header);

        // Declare utility vars
        byte[] data;
        ServerPacket ans;

        switch (req.proto) {
            case SERVREQ_GET_CLIENTS:
                byte[] clientData = server.clients.getAsData();
                data = concatData(header, clientData);
                ans = new ServerPacket(data);

                try {
                    send(ans);
                } catch (IOException e) {
                    System.out.println("Could not send answer (" + e.toString() + ")");
                }

                /*if (!pendant.contains(req.reqid)) {
                    pendant.add(req.reqid);
                    byte[] clientData = server.clients.getAsData();
                    ans = new ServerPacket(concatData(header, clientData));
                    packets.add(ans);
                }

                else
                    System.out.println("Duplicated packet. Dropped (" + req.reqid + ")");
                    */

                break;

            case SERVREQ_DISCONNECT:
                server.disconnectClient(this);
                thread.interrupt();
                break;

            case SERVER_ACKNOWLEDGEMENT:
                Integer reqid = req.reqid;

                System.out.println("ACK Received (" + reqid + ")");

                if (pendant.contains(reqid)) {
                    if (GENERATE_REQUEST_ID_ONLINE) {
                        try {
                            if (NetworkUtilities.releaseRequestId(reqid))
                                System.out.println("ID Released: " + reqid);
                            else
                                System.out.printf("Could not release ID: " + reqid);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    pendant.remove(reqid);
                }

                else
                    System.out.println("ACK Dropped (" + reqid + ")");

                break;
        }
    }

    @Override
    public void run() {
        while (server.isRunning()) try {
            Thread.sleep(SERVER_PACKET_SLEEP_TIME);
            ServerPacket packet = (ServerPacket) packets.take();

            if (pendant.contains(new Integer(packet.reqid))) {
                packets.add(packet);
                send(packet);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            break;
        }

        System.out.println("Thread stopped (" + getAddress().toString() + ")");
    }
}
