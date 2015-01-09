package storm;

public class ReceiveThread extends Thread {
    FrontClient client;

    public ReceiveThread(FrontClient client) {
        this.client = client;
        setDaemon(false);
    }

    @Override
    public void run() {
    }
}
