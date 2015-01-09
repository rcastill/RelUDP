package net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static net.NetworkConstants.*;
import static net.ServerPacket.*;

public class ServerConnection extends Connection implements Runnable {
    FrontClient client;

    Set<Integer> pendant = new HashSet<Integer>();
    BlockingQueue<Packet> packets = new LinkedBlockingQueue<Packet>();

    List<Packet> inbox = new ArrayList<Packet>();

    public final Thread thread;

    public ServerConnection(FrontClient client, InetAddress addr, int port) {
        super(client, addr, port);
        this.client = client;
        thread = new Thread(this);
        thread.setDaemon(true);
    }

    /**
     * Generates random request ID, then it is added
     * to pendant set and returned.
     * @return Random request id
     */
    private int generateRequestId() {
        Random random = new Random();
        int reqid;

        do reqid = random.nextInt();
        while (!pendant.add(reqid));

        return reqid;
    }

    private int TESTgenerateRequestId() throws IOException {
        int reqid;

        if (GENERATE_REQUEST_ID_ONLINE) {
            reqid = NetworkUtilities.getRequestId();
            pendant.add(reqid);
        }

        else {
            Random random = new Random();
            do reqid = random.nextInt();
            while (!pendant.add(reqid));
        }

        return reqid;
    }

    private void putRequest(int proto) throws IOException {
        int reqid = TESTgenerateRequestId();
        ServerPacket p = new ServerPacket(proto, reqid);
        packets.add(p);
    }

    public void putClientsPacketRequest() throws IOException {
        putRequest(SERVREQ_GET_CLIENTS);
    }

    public void putDisconnectionSignal() throws IOException {
        int reqid = TESTgenerateRequestId();
        ServerPacket packet = new ServerPacket(SERVREQ_DISCONNECT, reqid);
        packets.add(packet);
    }

    private void sendAcknowledgement(int reqid) {
        ByteBuffer buffer = ByteBuffer.allocate(HEADER_SIZE);
        buffer.putInt(SERVER_ACKNOWLEDGEMENT);
        buffer.putInt(reqid);

        try {
            send(new Packet(buffer.array()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ServerPacketInterpreter getInterpreter() {
        return new ServerPacketInterpreter(inbox);
    }

    @Override
    public void process(byte[] buffer) {
        byte[] header = new byte[HEADER_SIZE];
        System.arraycopy(buffer, 0, header, 0, header.length);

        ServerPacket ans = new ServerPacket(buffer);

        switch (ans.proto) {
            case SERVREQ_GET_CLIENTS:
                if (pendant.contains(ans.reqid)) {
                    System.out.println("Received (" + ans.reqid + ")");
                    pendant.remove(new Integer(ans.reqid));

                    if (GENERATE_REQUEST_ID_ONLINE) {
                        try {
                            if (NetworkUtilities.releaseRequestId(ans.reqid))
                                System.out.println("ID Released: " + ans.reqid);

                            else
                                System.out.println("Could not release ID: " + ans.reqid);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    ClientsPacket clientsData = null;

                    try {
                        clientsData = new ClientsPacket(ans);
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    }

                    if (clientsData != null)
                        inbox.add(clientsData);
                }

                // Impossible
                else
                    System.out.println("Packet duplicated. Dropped.");

                //sendAcknowledgement(ans.reqid);
                break;

            case SERVREQ_DISCONNECT:
                thread.interrupt();
                break;

            case SERVNOTF_CLIENT_DISCONNECTED:
            case SERVNOTF_CLIENT_CONNECTED:
                ConnectionPacket connectionPacket = null;

                try {
                    connectionPacket = new ConnectionPacket(ans);
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }

                if (connectionPacket != null) {
                    boolean shouldDrop = false;

                    for (Packet anInbox : inbox)
                        if (anInbox.compare(connectionPacket)) {
                            shouldDrop = true;
                            break;
                        }

                    if (shouldDrop) {
                        System.out.println("Notification duplicated. Dropped");
                        break;
                    }

                    for (Packet packet : inbox)
                        if (packet.compare(connectionPacket))


                    inbox.add(connectionPacket);

                    System.out.printf("Client ");
                    if (connectionPacket.isConnectedNotf())
                        System.out.printf("connected");

                    else
                        System.out.printf("disconnected");

                    System.out.println(connectionPacket.address.toString());
                    sendAcknowledgement(ans.reqid);
                }

                break;

            default:
                System.out.println("Unknown protocol (" + ans.proto + ") Dropped.");
                break;
        }
    }

    @Override
    public void run() {
        while (client.isRunning()) try {
            Thread.sleep(SERVER_PACKET_SLEEP_TIME); // 1 sec separation between requests
            ServerPacket packet = (ServerPacket) packets.take();

            if (pendant.contains(packet.reqid)) {
                packets.add(packet);
                send(packet);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            break;
        }

        System.out.println("Done");
    }
}
