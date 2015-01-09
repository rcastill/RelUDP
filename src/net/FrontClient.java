package net;

import java.io.IOException;
import java.net.*;

import static net.NetworkConstants.*;

public class FrontClient extends DatagramSocket implements Runnable {
    private boolean running = true;
    byte[] buffer = new byte[BUFFER_SIZE];

    ConnectionManager p2p = new ConnectionManager();
    ServerConnection server;

    public final Thread thread;

    public FrontClient(String addr, int port) throws SocketException, UnknownHostException {
        super(port, InetAddress.getByName(addr));
        thread = new Thread(this);
        thread.setDaemon(true);
    }

    public boolean isRunning() {
        return running;
    }

    public ServerConnection setServer(InetAddress addr, int port) {
        server = new ServerConnection(this, addr, port);
        return server;
    }

    public ServerConnection setServer(String addr, int port) throws UnknownHostException {
        return setServer(InetAddress.getByName(addr), port);
    }

    public ServerConnection setServer(Address address) {
        return setServer(address.addr, address.port);
    }

    public ServerConnection getServer() {
        return server;
    }

    public void halt() {
        running = false;
        thread.interrupt();
    }

    @Override
    public void run() {
        while (running) try {
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

            // Reveive packet
            receive(packet);

            InetAddress addr = packet.getAddress();
            int port = packet.getPort();

            if (server != null)
                if (server.getInetAddress().equals(addr)) {
                    server.process(buffer);
                    //continue;
                    break;
                }

            P2PConnection client = (P2PConnection) p2p.get(addr);

            if (client != null)
                client.process(buffer);

            else
                System.out.println("Unregistered P2PConnection");

        } catch (IOException e) {
            e.printStackTrace();
        }

        close();
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        FrontClient client = new FrontClient("localhost", 5314);
        ServerConnection server = client.setServer("localhost", 24121);

        client.thread.start();
        server.thread.start();

        server.putClientsPacketRequest();
        ServerPacketInterpreter interpreter = server.getInterpreter();

        ClientsPacket clientsPacket;

        while ((clientsPacket = interpreter.getClientsPacket()) == null) {
            System.out.println("Sleeping " + SERVER_PACKET_SLEEP_TIME / 1000 + "s");
            Thread.sleep(SERVER_PACKET_SLEEP_TIME);
        }

        System.out.println("Clients");
        System.out.println(clientsPacket.toString());

        client.thread.join();
    }
}
