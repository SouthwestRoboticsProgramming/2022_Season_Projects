package frc.messenger.server;

import java.net.ServerSocket;
import java.net.Socket;

public final class ClientConnectThread extends Thread {
    private final MessengerServer server;
    private final int port;

    public ClientConnectThread(MessengerServer server, int port) {
        this.server = server;
        this.port = port;
    }

    @Override
    public void run() {
        try {
             ServerSocket server = new ServerSocket(port);

             while (true) {
                 Socket client = server.accept();
                 new ClientHandlerThread(this.server, client).start();
             }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
