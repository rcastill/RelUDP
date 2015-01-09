package storm;

import java.net.InetAddress;

/**
 * Utility class designed to provide common
 * tools for networking.
 */
public class NetworkUtilities {
    /**
     * Converts String representation of IP
     * (X.X.X.X) to an integer
     * @param ip String representation
     * @return Conversion
     */
    public static int ipToInteger(String ip) {
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

    public static int ipToInteger(InetAddress addr) {
        String repr = addr.toString();
        String[] split = repr.split("/");
        return ipToInteger(split[1]);
    }

    /**
     * Converts integer representaion of IP to
     * String representation of IP (X.X.X.X)
     * @param ip Integer representation
     * @return Conversion
     */
    public static String integerToIp(int ip) {
        // Initialize result
        String result = "";

        // Populate result (->)
        for (int i = Integer.BYTES - 1; i >= 0; i--) {
            result += (ip >> Byte.SIZE * i) & 0xff;
            result += (i != 0) ? "." : "";
        }

        return result;
    }


}
