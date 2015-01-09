package storm;

import java.util.*;

public class External {
    private int expected = 0;

    Acknowledgement acknowledgement;

    private Queue<OLD_Packet> finalPackets;
    private List<OLD_Packet> packetRegister;

    public External() {
        finalPackets = new LinkedList<OLD_Packet>();
        packetRegister = new ArrayList<OLD_Packet>();
        acknowledgement = new Acknowledgement();
    }

    public OLD_Packet getPacket() {
        return finalPackets.poll();
    }

    public void processPacket(OLD_Packet packet) {
        if (packet.seq == expected) {
            finalPackets.add(packet);
            boolean scanningRegister = true;

            while (scanningRegister) {
                expected++;

                int i; for (i = 0; i < packetRegister.size(); i++)
                    if (packetRegister.get(i).seq == expected) {
                        finalPackets.add(packetRegister.remove(i));
                        break;
                    }

                if (i == packetRegister.size())
                    scanningRegister = false;
            }
        }

        else {
            packetRegister.add(packet);
            packetRegister.sort(new Comparator<OLD_Packet>() {
                @Override
                public int compare(OLD_Packet o1, OLD_Packet o2) {
                    return o1.seq - o2.seq;
                }
            });
        }

        int updateStatus = acknowledgement.update(expected - 1);

        switch (updateStatus) {
            case NetworkConstants.DELAYED:
                System.out.println("POOR CONNECTION");
                break;

            case NetworkConstants.PACKAGE_LOSS:
                System.out.println("LOST PACKAGES");
                break;

            case NetworkConstants.DUPLICATED:
                System.out.println("DUPLICATED");

            case NetworkConstants.UPDATE_SUCCESS:
                System.out.println("SUCCESS");
                break;
        }
    }
}
