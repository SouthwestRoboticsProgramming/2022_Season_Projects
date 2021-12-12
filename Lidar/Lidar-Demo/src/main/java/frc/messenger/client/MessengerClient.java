package frc.messenger.client;

import java.io.*;
import java.net.Socket;
import java.util.function.BiConsumer;

public class MessengerClient {
    private static final String LISTEN = "_Listen";
    private static final String UNLISTEN = "_Unlisten";
    private static final String HEARTBEAT = "_Heartbeat";

    private final Socket socket;
    private final DataInputStream in;
    private final DataOutputStream out;
    private BiConsumer<String, byte[]> callback = (a, b) -> {};

    public MessengerClient(String host, int port, String name) {
        try {
            socket = new Socket(host, port);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            out.writeUTF(name);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void sendMessage(String type, byte[] data) {
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

    public void listen(String type) {
        sendMessage(LISTEN, encodeString(type));
    }

    public void unlisten(String type) {
        sendMessage(UNLISTEN, encodeString(type));
    }

    public void setCallback(BiConsumer<String, byte[]> callback) {
        this.callback = callback;
    }

    public void read() {
        try {
            sendMessage(HEARTBEAT, new byte[0]);

            while (in.available() > 0) {
                readMessage();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void disconnect() {
        try {
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

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
