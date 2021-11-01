package frc.taskmanager.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class MessageServerClientConnection extends Thread {
    private static final int HEARTBEAT_TIMEOUT = 1000;

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
            origin = manager.getTask(in.readUTF());
            System.out.println("Message server client has identified as task '" + origin.getName() + "'");
            heartbeatTimer = HEARTBEAT_TIMEOUT;
            return;
        }

        String messageType = in.readUTF();
        int dataLen = in.readInt();
        byte[] messageData = new byte[dataLen];
        in.readFully(messageData);

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
            out.writeUTF(msg.getType());
            out.writeInt(msg.getData().length);
            out.write(msg.getData());
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
        } catch (IOException e) {
            System.err.println("Exception while handling client connection:");
            e.printStackTrace();
        }
    }
}
