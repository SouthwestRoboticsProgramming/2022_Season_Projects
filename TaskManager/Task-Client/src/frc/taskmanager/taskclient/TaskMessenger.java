package frc.taskmanager.taskclient;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.function.BiConsumer;

public class TaskMessenger {
    private final Socket socket;
    private final DataInputStream in;
    private final DataOutputStream out;
    private BiConsumer<String, byte[]> messageCallback = (t, d) -> {};

    public TaskMessenger(String messageServerHost, int messageServerPort, String name) {
        try {
            socket = new Socket(messageServerHost, messageServerPort);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            out.writeUTF(name);
        } catch (Exception e) {
            throw new RuntimeException("Failed to connect to message server", e);
        }
    }

    public void sendMessage(String type, byte[] data) {
        try {
            out.writeUTF(type);
            out.writeInt(data.length);
            out.write(data);
        } catch (IOException e) {
            System.err.println("Failed to send message:");
            e.printStackTrace();
        }
    }

    public void read() {
        try {
            out.writeUTF("_Heartbeat");
            out.writeInt(0);

            while (in.available() > 0) {
                String type = in.readUTF();
                int len = in.readInt();
                byte[] data = new byte[len];
                in.readFully(data);

                messageCallback.accept(type, data);
            }
        } catch (IOException e) {
            System.err.println("Exception while reading messages:");
            e.printStackTrace();
        }
    }

    public void disconnect() {
        try {
            socket.close();
        } catch (Exception e) {
            System.err.println("Exception while disconnecting:");
            e.printStackTrace();
        }
    }

    public void setMessageCallback(BiConsumer<String, byte[]> callback) {
        messageCallback = callback;
    }
}
