package storm;

/**
 * Contains up to 8 boolean states
 */
public class StateByte {
    // Constants for slot indexing
    public static final int SLOT1 = 0;
    public static final int SLOT2 = 1;
    public static final int SLOT3 = 2;
    public static final int SLOT4 = 3;
    public static final int SLOT5 = 4;
    public static final int SLOT6 = 5;
    public static final int SLOT7 = 6;
    public static final int SLOT8 = 7;

    // Boolean container
    private boolean[] states = new boolean[Byte.SIZE];

    // Void Constructor
    public StateByte() {}

    /**
     * Constructs StateByte from a byte bitfield. It can
     * be generated by {@link #getByte()}
     * @param stateByte Bitfield
     */
    public StateByte(int stateByte) {
        // Gets each bit and sets it in array
        for (int i = 0; i < Byte.SIZE; i++)
            states[i] = (((stateByte >> i) & 0x1) == 1);
    }

    /**
     * Sets state to a slot
     * @param slot Slot to be set
     * @param value State to be set
     */
    public void set(int slot, boolean value) {
        states[slot] = value;
    }

    /**
     * Gets a state from a slot
     * @param slot Slot to index
     * @return State in slot
     */
    public boolean get(int slot) {
        return states[slot];
    }

    /**
     * Makes bitfield from state array
     * @return Bitfield
     */
    public int getByte() {
        // Initialize byte
        int stateByte = 0;

        // Populate each bit
        for (int i = 0; i < Byte.SIZE; i++)
            stateByte |= (states[i]) ? (1 << i) : 0;

        return stateByte;
    }

    /**
     * Generates debugging string with syntax
     * [v, v, v, v, v, v, v, v] where v are the
     * values to each state slot
     * @return String representation
     */
    @Override
    public String toString() {
        // Initialize string
        String result = "[";

        // Populate with "true"/"false"
        for (int i = 0; i < Byte.SIZE; i++) {
            result += (states[i]) ? "true" : "false";
            result += (i != Byte.SIZE - 1) ? ", " : "]";
        }

        return result;
    }
}