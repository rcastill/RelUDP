package storm;

import net.*;
import net.NetworkUtilities;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static net.NetworkUtilities.concatData;

public class Test implements Runnable {
    BlockingQueue<Integer> integerBlockingQueue = new LinkedBlockingQueue<Integer>();

    public static void main0(String[] args) {
        ByteBuffer wbuffer = ByteBuffer.allocate(NetworkConstants.BUFFER_SIZE);
        wbuffer.putInt(5);
        wbuffer.putInt(4);
        wbuffer.putInt(2);
        wbuffer.putInt(8);
        wbuffer.putInt(-2);
        wbuffer.putInt(0);

        ByteBuffer rbuffer = ByteBuffer.wrap(wbuffer.array());

        for (int i = 0; i < 6; i++)
            System.out.println(rbuffer.getInt());
    }

    public static void main1(String[] args) {
        List<Integer> integerList = new ArrayList<Integer>();
        int n = 10, i = 0;

        while (i++ < n)
            integerList.add(new Random().nextInt(10));

        printList(integerList);
        integerList.sort(new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                //return o1 - o2; // less -> great
                return o2 - o1; // great -> less
            }
        });
        printList(integerList);
    }

    public static void printList(List list) {
        for (Object e : list)
            System.out.printf(e + " ");

        System.out.println();
    }

    public static void main2(String[] args) throws SocketException {
        System.out.println(Integer.toBinaryString(0x40000000));
        System.out.println(Integer.toBinaryString(0x40000000>>1));

        DatagramSocket socket = new DatagramSocket();
    }

    public static void main3(String[] args) throws IOException {
        DatagramSocket socket = new DatagramSocket();
        ByteBuffer number = ByteBuffer.allocate(Integer.BYTES);
        byte[] data = number.array();
        Random random = new Random();

        for (int i = 0; i < 10; i++) {
            int code = random.nextInt();
            number.putInt(code);
            socket.send(new DatagramPacket(data, data.length, InetAddress.getByName("104.131.173.250"), 5428));
            number.clear();

            System.out.println("Packet sent. (" + code + ")");
        }

        number.putInt(NetworkConstants.SERVER_CONNECT_REQUEST);
        socket.send(new DatagramPacket(data, data.length, InetAddress.getByName("104.131.173.250"), 5428));
        System.out.println("Packet sent. (Connection)");
        socket.close();
    }

    public static void main4(String[] args) {
        String test = "/192.168.131.23";

        String[] split = test.split("/");

        for (String s : split)
            System.out.println((s.equals("")) ? "EMPTY" : s);
    }

    public static void main5(String[] args) throws IOException {
        DatagramSocket client = new DatagramSocket();

        ByteBuffer p1 = ByteBuffer.allocate(Integer.BYTES * 3);
        ByteBuffer p2 = ByteBuffer.allocate(Integer.BYTES * 3);
        ByteBuffer p3 = ByteBuffer.allocate(Integer.BYTES * 3);

        p1.putInt(371192);
        p1.putInt(41);
        p1.putInt(23);

        p2.putInt(371192);
        p2.putInt(5428);
        p2.putInt(3123);

        p3.putInt(371192);
        p3.putInt(42);
        p3.putInt(0);

        byte[] p1arr = p1.array();
        byte[] p2arr = p2.array();
        byte[] p3arr = p3.array();

        DatagramPacket packet1 = new DatagramPacket(p1arr, p1arr.length, InetAddress.getByName("localhost"), 5428);
        DatagramPacket packet2 = new DatagramPacket(p2arr, p2arr.length, InetAddress.getByName("localhost"), 5428);
        DatagramPacket packet3 = new DatagramPacket(p3arr, p3arr.length, InetAddress.getByName("localhost"), 5428);

        client.send(packet1);
        client.send(packet2);
        client.send(packet3);
        client.close();
    }

    public static void main6(String[] args) {
        ByteBuffer b1 = ByteBuffer.allocate(Integer.BYTES);
        ByteBuffer b2 = ByteBuffer.allocate(Integer.BYTES * 2);

        b1.putInt(42);

        b2.putInt(32);
        b2.putInt(5428);

        byte[] b1arr = b1.array();
        byte[] b2arr = b2.array();

        ByteBuffer rb = ByteBuffer.wrap(concatData(b1arr, b2arr));

        for (int i = 0; i < 3; i++)
            System.out.println("rb (" + i + ") = " + rb.getInt());

    }

    public static void main7(String[] args) throws InterruptedException {
        Test test = new Test();
        Thread thread = new Thread(test);
        thread.setDaemon(true);
        thread.start();

        System.out.println("Thread (" + thread.getId() + ") started");

        test.integerBlockingQueue.add(12);
        test.integerBlockingQueue.add(42);

        Thread.sleep(100);
        thread.interrupt();

        System.out.println("Bye!");
    }

    @Override
    public void run() {
        // for main7
        while (true) {
            if (Thread.currentThread().isInterrupted())
                break;
            System.out.println("Anal sex");
        }

        System.out.println("Thread (" + Thread.currentThread().getId() + ") finished");
    }

    public static void main8(String[] args) {
        ByteBuffer buff1 = ByteBuffer.allocate(Integer.BYTES * 2);
        ByteBuffer buff2 = ByteBuffer.allocate(Integer.BYTES * 3);

        buff1.putInt(42);
        buff1.putInt(13);

        buff2.putInt(124);
        buff2.putInt(432);
        buff2.putInt(531);

        byte[] data = buff1.array();
        data = net.NetworkUtilities.concatData(data, buff2.array());

        ByteBuffer reader = ByteBuffer.wrap(data);

        for (int t = 0; t < 5; t++)
            System.out.println("t" + t + " = " + reader.getInt());

        System.out.println("END OF BUFFER");
    }

    @SuppressWarnings("BooleanConstructorCall")
    public static void main9(String[] args) {
        Boolean bool1 = new Boolean("TRUE");
        Boolean bool2 = new Boolean("aiosjdoasjdas");

        System.out.println(bool1);
        System.out.println(bool2);
    }

    public static void main10(String[] args) throws IOException {
        int reqid = net.NetworkUtilities.getRequestId();
        System.out.println("reqid = " + reqid);

        System.out.println(NetworkUtilities.releaseRequestId(reqid));
        System.out.println(NetworkUtilities.releaseRequestId(reqid));
    }

    public static void main(String[] args) {
        TestFather father = new TestFather();
        TestSon1 son1 = new TestSon1();
        TestSon2 son2 = new TestSon2();

        ((TestFather) son1).jerkOff();
        ((TestFather) son2).jerkOff();
    }

}
