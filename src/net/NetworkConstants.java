package net;

public class NetworkConstants {
    public static final int BUFFER_SIZE = 250;
    public static final int PROTOCOL_SIZE = Integer.BYTES;
    public static final int REQID_SIZE = Integer.BYTES;
    public static final int SERVER_PACKET_SLEEP_TIME = 1000;

    public static final int SERVREQ_GET_CLIENTS = 0x32bc1625;
    public static final int SERVREQ_DISCONNECT = 0x18f647b0;
    public static final int SERVER_ACKNOWLEDGEMENT = 0xa8c88c;

    public static final int SERVNOTF_CLIENT_CONNECTED = 0x54138013;
    public static final int SERVNOTF_CLIENT_DISCONNECTED = 0x755aa895;

    public static boolean GENERATE_REQUEST_ID_ONLINE = true;

    public static int GetDataSize(int proto) {
        switch (proto) {
            default:
                return -1;
        }
    }
}
