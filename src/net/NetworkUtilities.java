package net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.nio.ByteBuffer;

public class NetworkUtilities {
    public static int getProtocol(byte[] buffer) {
        byte[] protocol = new byte[NetworkConstants.PROTOCOL_SIZE];
        System.arraycopy(buffer, 0, protocol, 0, protocol.length);
        return ByteBuffer.wrap(protocol).getInt();
    }

    public static byte[] concatData(byte[] half1, byte[] half2) {
        byte[] total = new byte[half1.length + half2.length];
        System.arraycopy(half1, 0, total, 0, half1.length);
        System.arraycopy(half2, 0, total, half1.length, half2.length);
        return total;
    }

    /**
     * Converts String representation of IP
     * (X.X.X.X) to an integer
     * @param ip String representation
     * @return Conversion
     */
    public static int addrToInteger(String ip) {
        // Split each "byte"
        String[] splitted = ip.split("\\.");

        // Must be 4 bytes (Integer.BYTES)
        if (splitted.length != Integer.BYTES)
            return -1;

        // Declare array of digits
        int[] parts = new int[Integer.BYTES];

        // Populate array
        for (int i = 0; i < parts.length; i++)
            parts[i] = Integer.parseInt(splitted[i]);

        // Initialize result
        int result = 0;

        // Process parts and merge into result
        for (int i = 0; i < parts.length; i++)
            // parts.length - 1 (3)
            result |= parts[3 - i] << i * Byte.SIZE;

        return result;
    }

    public static int addrToInteger(InetAddress addr) {
        String repr = addr.toString();
        String[] split = repr.split("/");
        return addrToInteger(split[1]);
    }

    public static int addrToInteger(Address address) {
        return addrToInteger(address.addr);
    }

    /**
     * Converts integer representaion of IP to
     * String representation of IP (X.X.X.X)
     * @param ip Integer representation
     * @return Conversion
     */
    public static String integerToStringAddr(int ip) {
        // Initialize result
        String result = "";

        // Populate result (->)
        for (int i = Integer.BYTES - 1; i >= 0; i--) {
            result += (ip >> Byte.SIZE * i) & 0xff;
            result += (i != 0) ? "." : "";
        }

        return result;
    }

    public static InetAddress integerToAddr(int ip) throws UnknownHostException {
        return InetAddress.getByName(integerToStringAddr(ip));
    }

    private static BufferedReader getUrlBufferedReader(String urlString) throws IOException {
        URL url = new URL(urlString);
        URLConnection conn = url.openConnection();
        conn.connect();
        return new BufferedReader(new InputStreamReader(conn.getInputStream()));
    }

    public static int getRequestId() throws IOException {
        BufferedReader in = getUrlBufferedReader("http://104.131.173.250/reludp/api/get-request-id.php");
        return Integer.parseInt(in.readLine());
    }

    public static boolean releaseRequestId(int reqid) throws IOException {
        BufferedReader in = getUrlBufferedReader
                ("http://104.131.173.250/reludp/api/release-request-id.php?reqid=" + reqid);
        return Boolean.parseBoolean(in.readLine());
    }
}
