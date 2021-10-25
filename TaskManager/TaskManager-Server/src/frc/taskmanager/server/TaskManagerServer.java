package frc.taskmanager.server;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TaskManagerServer {
    private final int port;
    private final int messagePort;
    private final File taskFolder;

    public TaskManagerServer(File taskFolder, int port, int messagePort) {
        this.taskFolder = taskFolder;
        this.port = port;
        this.messagePort = messagePort;
    }

    public void run() {
        ServerSocket socket;
        try {
            socket = new ServerSocket(port);
        } catch (IOException e) {
            throw new RuntimeException("Failed to bind to port " + port, e);
        }

        TaskManager taskManager = new TaskManager(taskFolder);
        MessageServer messageServer = new MessageServer(messagePort, taskManager);
        messageServer.start();

        while (true) {
            try {
                System.out.println("Waiting for incoming connections...");
                Socket clientSocket = socket.accept();
                ClientConnection connection = new ClientConnection(clientSocket, taskManager);
                System.out.println("Client has connected");
                connection.run();
            } catch (Exception e) {
                System.err.println("Exception handling client connection:");
                e.printStackTrace();
            }
        }
    }
}
