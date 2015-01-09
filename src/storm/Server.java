package storm;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;

public class Server extends DatagramSocket {
    private boolean running = true;
    //HashMap<InetAddress, External> clients = new HashMap<InetAddress, External>();
    BackClientManager clients = new BackClientManager(8);
    byte[] protocol = new byte[Integer.BYTES];
    byte[] buffer = new byte[NetworkConstants.BUFFER_SIZE];

    /**
     * Binds UDP port on HOST
     * @param host Hostname
     * @param port Portno
     * @throws UnknownHostException Invalid host
     * @throws SocketException Could not bind
     */
    public Server(String host, int port) throws UnknownHostException, SocketException {
        super(port, InetAddress.getByName(host));
    }



    public boolean isRunning() {
        return running;
    }

    public void mainloop() {
        while (running) try {
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            receive(packet);

            System.arraycopy(buffer, 0, protocol, 0, protocol.length);
            int proto = ByteBuffer.wrap(protocol).getInt();
            BackClient client = clients.getClientByAddress(packet.getAddress());

            if (client == null) {
                client = new BackClient(this, packet.getAddress(), packet.getPort());
                clients.add(client);
            }

            client.process(proto);
        } catch (IOException e) {
            e.printStackTrace();
        }

        close();
    }

    public static void main(String[] args) {
        try {
            String address = "localhost";
            int port = 5428;

            if (args.length == 2) {
                address = args[0];
                port = Integer.parseInt(args[1]);
            }

            Server server = new Server(address, port);
            server.mainloop();
        } catch (SocketException e) {
            System.out.println("Socket exception." + e.toString());
        } catch (UnknownHostException e) {
            System.out.println("Unknown host exception." + e.toString());
        }

    }
}
