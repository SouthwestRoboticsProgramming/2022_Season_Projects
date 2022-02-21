package frc.messenger.client;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.function.BiConsumer;

/**
 * Allows interfacing with the robot-wide messaging service, Messenger.
 * This allows multiple processes across different processors to communicate
 * and share data with each other.<br/>
 *
 * This communication is done through messages. A message consists of a
 * {@code String} indicating the type and an array of bytes, which can contain
 * any arbitrary data. A client can choose to listen to any type of message,
 * and will only receive messages they have explicitly listened to.
 *
 * @author rmheuer
 */
public class MessengerClient {
    private static final String LISTEN = "_Listen";
    private static final String UNLISTEN = "_Unlisten";
    private static final String HEARTBEAT = "_Heartbeat";
    private static final int TIMEOUT = 1000;

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private boolean noOp;
    private BiConsumer<String, byte[]> callback = (a, b) -> {};

    /**
     * Creates a new {@code MessengerClient} and attempts to connect to the
     * Messenger server at the given address. A name is given to help identify
     * the client in the server log.
     *
     * @param host host of messenger server
     * @param port port of messenger server
     * @param name name of this client
     * @param require whether to throw an exception if connection fails
     * @throws RuntimeException if connection required and failed
     */
    public MessengerClient(String host, int port, String name, boolean require) {
        try {
            socket = new Socket();

            InetSocketAddress address = new InetSocketAddress(host, port);
            socket.connect(address, TIMEOUT);

            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            out.writeUTF(name);

            noOp = false;
        } catch (Exception e) {
            if (require) {
                noOp = false;
                throw new RuntimeException(e);
            } else {
                System.out.println("Messenger connection failed, switching to no-op mode");
                noOp = true;
            }
        }
    }

    /**
     * Sends a message to the Messenger server. The message will be dispatched
     * to any other clients that are listening to the message type.
     *
     * @param type type of message
     * @param data message data
     */
    public void sendMessage(String type, byte[] data) {
        if (noOp) return;

        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream d = new DataOutputStream(b);

        try {
            d.writeUTF(type);
            d.writeInt(data.length);
            d.write(data);

            byte[] message = b.toByteArray();
            out.writeInt(message.length);
            out.write(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Indicates to the Messenger server that this client would like to listen
     * to messages of the given type.
     *
     * @param type message type to listen to
     */
    public void listen(String type) {
        if (noOp) return;

        sendMessage(LISTEN, encodeString(type));
    }

    /**
     * Indicates to the Messenger server that this client would no longer like
     * to listen to messages of the given type.
     *
     * @param type message type to stop listening to
     */
    public void unlisten(String type) {
        if (noOp) return;

        sendMessage(UNLISTEN, encodeString(type));
    }

    /**
     * Sets a callback for when a message is received from the Messenger server.
     *
     * @param callback message callback
     */
    public void setCallback(BiConsumer<String, byte[]> callback) {
        this.callback = callback;
    }

    /**
     * Reads in any available messages and indicates to the server that this client
     * is still connected. If this method is not called for too long, the server will
     * assume that the connection is dropped and disconnect.
     */
    public void read() {
        if (noOp) return;

        try {
            sendMessage(HEARTBEAT, new byte[0]);

            while (in.available() > 0) {
                readMessage();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Disconnects from the Messenger server. If this method is not called, the server
     * will still detect that the connection is lost, but it is good practice to call
     * this method to end the connection safely.
     */
    public void disconnect() {
        if (noOp) return;

        try {
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Encodes a string into length-prefixed UTF-8 encoding
    private byte[] encodeString(String str) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream d = new DataOutputStream(b);

        try {
            d.writeUTF(str);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return b.toByteArray();
    }

    // Reads in a message from the server
    private void readMessage() throws IOException {
        int length = in.readInt();
        byte[] data = new byte[length];
        in.readFully(data);

        DataInputStream d = new DataInputStream(new ByteArrayInputStream(data));
        String type = d.readUTF();
        int dataLength = d.readInt();
        byte[] messageData = new byte[dataLength];
        d.readFully(messageData);
        d.close();

        callback.accept(type, messageData);
    }
}