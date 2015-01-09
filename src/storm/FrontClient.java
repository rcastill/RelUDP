package storm;

import java.io.IOException;
import java.net.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class FrontClient extends DatagramSocket {
    private boolean running = true;

    ServerConnection server;
    Thread sendThread;
    Thread recvThread;

    byte[] buffer = new byte[NetworkConstants.BUFFER_SIZE];

    BlockingQueue<Packet> sendQueue = new LinkedBlockingDeque<Packet>();

    /**
     * Binds udp port on client
     * @param host Hostname
     * @param port Portno
     * @throws UnknownHostException Invalid hostname
     * @throws SocketException Could not bind
     */
    public FrontClient(String host, int port) throws IOException {
        super(port, InetAddress.getByName(host));
        initThreads();
    }

    public void setServer(InetAddress addr, int port) {
        server = new ServerConnection(this, addr, port);
    }

    public void setServer(String addr, int port) throws UnknownHostException {
        server = new ServerConnection(this, addr, port);
    }

    public boolean isRunning() {
        return running;
    }

    private void initThreads() {
        sendThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (running) try {
                    Packet packet = sendQueue.take();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        recvThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (running) try {
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    receive(packet);


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        sendThread.setDaemon(true);
        recvThread.setDaemon(true);
    }
}
