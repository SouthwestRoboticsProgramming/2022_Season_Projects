package frc.taskmanager.taskclient;

import java.io.*;
import java.net.Socket;
import java.util.function.BiConsumer;

/**
 * Allows communication with the TaskManager Client
 * through the TaskManager Server. This class should
 * be used from within the task processes, as it
 * requires you to identify which task you are.
 *
 * @author rmheuer
 */
public class TaskMessenger {
    private final Socket socket;
    private final DataInputStream in;
    private final DataOutputStream out;
    private BiConsumer<String, byte[]> messageCallback = (t, d) -> {};

    /**
     * Creates a new instance of this class and
     * connects to the message server at the given
     * address.
     *
     * @param messageServerHost host of the message server, usually localhost
     * @param messageServerPort port of the message server on the host
     * @param name name of the task that is using this API
     */
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

    /**
     * Sends a message to the client. A message
     * comprises of a String representing the type
     * of message, and a byte array containing the
     * message data. The data can be any arbitrary
     * byte data, and the type can be any string
     * except for "_Heartbeat".
     *
     * @param type the message type
     * @param data the message data
     */
    public void sendMessage(String type, byte[] data) {
        try {
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream o = new DataOutputStream(b);
            o.writeUTF(type);
            o.writeInt(data.length);
            o.write(data);

            byte[] packet = b.toByteArray();
            out.writeInt(packet.length);
            out.write(packet);
        } catch (IOException e) {
            System.err.println("Failed to send message:");
            e.printStackTrace();
        }
    }

    /**
     * Reads in all messages that have been received.
     * This method should be called at least once
     * every 10 seconds to ensure the connection does
     * not time out.
     */
    public void read() {
        try {
            sendMessage("_Heartbeat", new byte[0]);

            while (in.available() > 0) {
                System.out.println("Input data is available");
                int packetLen = in.readInt();
                System.out.println("Reading " + packetLen + " bytes");
                byte[] packet = new byte[packetLen];
                in.readFully(packet);
                DataInputStream i = new DataInputStream(new ByteArrayInputStream(packet));

                String type = i.readUTF();
                int len = i.readInt();
                byte[] data = new byte[len];
                i.readFully(data);

                System.out.println("Received message: " + type + " with " + len + " data bytes");
                messageCallback.accept(type, data);
            }
        } catch (IOException e) {
            System.err.println("Exception while reading messages:");
            e.printStackTrace();
        }
    }

    /**
     * Disconnects from the message server. No methods
     * in this class should be called after disconnecting,
     * as they mey throw an exception, and will not send
     * or receive any messages.
     */
    public void disconnect() {
        try {
            socket.close();
        } catch (Exception e) {
            System.err.println("Exception while disconnecting:");
            e.printStackTrace();
        }
    }

    /**
     * Sets the incoming message callback. The callback
     * will be called every time a message is received
     * from the client.
     *
     * @param callback message callback
     */
    public void setMessageCallback(BiConsumer<String, byte[]> callback) {
        messageCallback = callback;
    }
}
