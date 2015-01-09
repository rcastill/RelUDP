package net;

import java.io.IOException;
import java.net.*;

import static net.NetworkConstants.*;

public class Server extends DatagramSocket {
    private boolean running = true;
    private byte[] buffer = new byte[NetworkConstants.BUFFER_SIZE];

    ConnectionManager clients = new ConnectionManager();

    public Server(String addr, int port) throws SocketException, UnknownHostException {
        super(port, InetAddress.getByName(addr));
    }

    public boolean isRunning() {
        return running;
    }

    public void halt() {
        running = false;
    }

    private void putConnectionPacket(BackClient client, int notf) {
        for (Connection obj : clients) {
            BackClient conn = (BackClient) obj;
            int id = 0;

            if (GENERATE_REQUEST_ID_ONLINE) {
                try {
                    id = NetworkUtilities.getRequestId();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                conn.pendant.add(id);
            }

            else
                id = conn.generateRequestId();

            conn.packets.add(new ConnectionPacket(notf, id, client));
        }
    }

    public void disconnectClient(BackClient client) {
        clients.remove(client);
        putConnectionPacket(client, SERVNOTF_CLIENT_DISCONNECTED);
    }

    public void mainloop() {
        while (running) try {
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

            // receive packet
            receive(packet);

            InetAddress addr = packet.getAddress();
            int port = packet.getPort();

            BackClient client = (BackClient) clients.get(addr);

            // Add client
            if (client == null) {
                client = new BackClient(this, addr, port);
                putConnectionPacket(client, SERVNOTF_CLIENT_CONNECTED);
                clients.add(client);
                client.thread.start();
            }

            // Process buffer
            client.process(buffer);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws SocketException, UnknownHostException {
        String addr = "localhost";
        int port = 5000;

        if (args.length == 2) {
            addr = args[0];
            port = Integer.parseInt(args[1]);
        }

        Server server = new Server(addr, port);
        server.mainloop();
    }
}
