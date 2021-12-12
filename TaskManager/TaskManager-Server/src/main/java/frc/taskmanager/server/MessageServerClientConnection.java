package frc.taskmanager.server;

import java.io.*;
import java.net.Socket;

public class MessageServerClientConnection extends Thread {
    private static final int HEARTBEAT_TIMEOUT = 300;

    private final Socket socket;
    private final TaskManager manager;
    private final DataInputStream in;
    private final DataOutputStream out;
    private Task origin = null;
    private int heartbeatTimer;

    public MessageServerClientConnection(Socket socket, TaskManager manager) {
        this.socket = socket;
        this.manager = manager;
        try {
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
        } catch (Exception e) {
            throw new RuntimeException("Failed to get streams from socket", e);
        }
    }

    private void readPacket() throws IOException {
        if (origin == null) {
            String name = in.readUTF();
            origin = manager.getTask(name);
            if (origin == null) {
                System.out.println("Warning: Message server client identified as nonexistent task '" + name + "'");
                return;
            }
            System.out.println("Message server client has identified as task '" + origin.getName() + "'");
            heartbeatTimer = HEARTBEAT_TIMEOUT;
            return;
        }

        int packetLen = in.readInt();
        byte[] packet = new byte[packetLen];
        in.readFully(packet);
        System.out.println(packet.length);

        DataInputStream i = new DataInputStream(new ByteArrayInputStream(packet));
        String messageType = i.readUTF();
        int dataLen = i.readInt();
        byte[] messageData = new byte[dataLen];
        i.readFully(messageData);

        //System.out.println(messageType);
        if (messageType.equals("_Heartbeat")) {
            heartbeatTimer = HEARTBEAT_TIMEOUT;
            //System.out.println("Heartbeat!");
            return;
        }

        manager.queueClientboundMessage(new ClientboundMessage(origin.getName(), messageType, messageData));
    }

    private void flushMessages() throws IOException {
        if (origin == null) return;
        TaskboundMessage msg;
        while ((msg = origin.pollMessageQueue()) != null) {
            System.out.println("Writing message: " + msg.getType());
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream o = new DataOutputStream(b);

            o.writeUTF(msg.getType());
            o.writeInt(msg.getData().length);
            o.write(msg.getData());

            byte[] data = b.toByteArray();
            out.writeInt(data.length);
            out.write(data);
        }
    }

    @Override
    public void run() {
        try {
            heartbeatTimer = Integer.MAX_VALUE;
            while (heartbeatTimer > 0) {
                heartbeatTimer--;

                while (in.available() > 0) {
                    readPacket();
                }

                flushMessages();

                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("Message server client '" + (origin == null ? "unknown" : origin.getName()) + "' disconnected due to heartbeat timeout");
            socket.close();
        } catch (Throwable e) {
            System.err.println("Exception while handling client connection:");
            e.printStackTrace();
        }
    }
}
