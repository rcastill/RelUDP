package storm;

public class Acknowledgement {
    public static final int BYTES = Integer.BYTES * 2;

    private int ack;
    private int ackBitfield;

    public Acknowledgement(int ack, int ackBitfield) {
        this.ack = ack;
        this.ackBitfield = ackBitfield;
    }

    public Acknowledgement() {
        ack = -1;
        ackBitfield = 0;
    }

    public int getAck() {
        return ack;
    }

    public int getAckBitfield() {
        return ackBitfield;
    }

    public int update(int ack) {
        AckUpdateStatus status = new AckUpdateStatus();

        int diff = Math.abs(ack - this.ack);
        int add = 1 << (diff - 1);

        if (diff > NetworkConstants.MEMLIMIT)
            return NetworkConstants.DELAYED;

        if (ack > this.ack) {
            this.ack = ack;

            if (ack > NetworkConstants.MEMLIMIT) {
                int rev = NetworkConstants.LEFTMOST_BIT;

                for (int i = 0; i < diff; i++) {
                    if ((ackBitfield & rev) == 0)
                        return NetworkConstants.PACKAGE_LOSS;

                    rev >>= 1;
                }
            }

            ackBitfield <<= diff;
            ackBitfield |= add;
        } else if (this.ack > ack) {
            if ((add & ackBitfield) == 0)
                ackBitfield |= add;

            else
                return NetworkConstants.DUPLICATED;
        } else {
            // DUPLICATED (this.ack == ack)
            return NetworkConstants.DUPLICATED;
        }

        return NetworkConstants.UPDATE_SUCCESS;
    }
}