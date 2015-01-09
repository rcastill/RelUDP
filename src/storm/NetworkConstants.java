package storm;

public final class NetworkConstants {
    public static final int BUFFER_SIZE = 256;
    public static final int DUPLICATED = 0;
    public static final int DELAYED = 1;
    public static final int PACKAGE_LOSS = 2;
    public static final int UPDATE_SUCCESS = 3;
    public static final int LEFTMOST_BIT = 0x40000000;
    public static final int MEMLIMIT = 32;
    public static final int SERVER_CONNECT_REQUEST = 0x8c979e;
    public static final int SERVREQ_DISCONNECT = 0x146fbf2c;
    public static final int SERVREQ_CONNECTED_CLIENTS = 0x7de15f3d;
    public static final int SERVER_ACKNOWLEDGEMENT = 0x5a0ad1f4;

    public static final int CLIENT_PORT = 5428;

    public static final int SERVREQ_TEST = 0x3ae3e;

    public static int GetPacketSize(int proto) {
        switch (proto) {
            case SERVREQ_DISCONNECT:
                return 0;

            case SERVREQ_CONNECTED_CLIENTS:
                return 0;

            case SERVREQ_TEST:
                return Integer.BYTES * 2;

            case SERVER_ACKNOWLEDGEMENT:
                return Integer.BYTES;

            default:
                return -1;
        }
    }
}
