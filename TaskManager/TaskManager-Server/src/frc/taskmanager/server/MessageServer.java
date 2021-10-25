package frc.taskmanager.server;

import java.net.ServerSocket;
import java.net.Socket;

public class MessageServer extends Thread {
    private final int port;
    private final TaskManager manager;

    public MessageServer(int port, TaskManager manager) {
        super("Message Server");
        this.port = port;
        this.manager = manager;
    }

    @Override
    public void run() {
        ServerSocket socket;
        try {
            socket = new ServerSocket(port);
        } catch (Exception e) {
            throw new RuntimeException("Failed to bind to port " + port);
        }

        System.out.println("Message server started");
        while (true) {
            try {
                Socket client = socket.accept();
                System.out.println("Accepting connection to message server");
                new MessageServerClientConnection(client, manager).start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
