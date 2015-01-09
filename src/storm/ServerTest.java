package storm;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;

public class ServerTest extends DatagramSocket {
    boolean running = true;
    byte[] protocol = new byte[Integer.BYTES];
    byte[] buffer;

    public ServerTest(String addr, int port, int buffSize) throws UnknownHostException, SocketException {
        super(port, InetAddress.getByName(addr));
        buffer = new byte[buffSize];
    }

    public ServerTest(String addr, int port) throws SocketException, UnknownHostException {
        this(addr, port, 256);
    }

    public void mainloop() {
        while (running) {
            DatagramPacket protoPacket = new DatagramPacket(buffer, buffer.length);
            ByteBuffer byteBuffer;

            try {
                receive(protoPacket);
            } catch (IOException e) {
                System.out.println("Could not receive proto.");
            }

            for (int i = 0; i < 10; i++)
                System.out.println("buffer[" + i + "] = " + buffer[i]);

            System.arraycopy(buffer, 0, protocol, 0, protocol.length);
            int proto = ByteBuffer.wrap(protocol).getInt();

            System.out.println("Proto: " + proto);

            switch (proto) {
                case 371192:
                    System.out.println("case 0:");

                    byte[] data = new byte[Integer.BYTES * 2];
                    System.arraycopy(buffer, 4, data, 0, data.length);

                    byteBuffer = ByteBuffer.wrap(data);
                    System.out.println("Number 1: " + byteBuffer.getInt());
                    int inst = byteBuffer.getInt();
                    System.out.println("Number 2: " + inst);

                    if (inst == 0)
                        running = false;

                    break;

                default:
                    System.out.println("Unknown protocol");
                    break;
            }
        }

        close();

    }

    public static void main(String[] args) throws SocketException, UnknownHostException {
        ServerTest server = new ServerTest("localhost", 5428);
        server.mainloop();
    }
}
