package frc.messenger.server;

import java.io.*;
import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class ClientHandlerThread extends Thread {
    private static final String LISTEN = "_Listen";
    private static final String UNLISTEN = "_Unlisten";
    private static final String HEARTBEAT = "_Heartbeat";
    private static final int TIMEOUT = 500;

    private final MessengerServer server;
    private final Socket socket;
    private final Queue<Message> incoming;
    private boolean identified = false;
    private String name = "[Unknown]";
    private int timer = TIMEOUT;
    
    public ClientHandlerThread(MessengerServer server, Socket socket) {
        this.server = server;
        this.socket = socket;
        incoming = new ConcurrentLinkedQueue<>();
    }

    public void sendMessage(Message message) {
        incoming.add(message);
    }
    
    private void loadName(String name) {
        System.out.println("Client identified as '" + name + "'");
        this.name = name;
    }
    
    private void readMessage(DataInputStream in) throws IOException {
        int length = in.readInt();

        byte[] data = new byte[length];
        in.readFully(data);

        DataInputStream i = new DataInputStream(new ByteArrayInputStream(data));
        String type = i.readUTF();
        int dataLength = i.readInt();
        byte[] messageData = new byte[dataLength];
        i.readFully(messageData);

        if (type.equals(HEARTBEAT)) {
            timer = TIMEOUT;
        } else if (type.equalsIgnoreCase(LISTEN)) {
            DataInputStream din = new DataInputStream(new ByteArrayInputStream(messageData));
            String listen = din.readUTF();
            din.close();

            server.listen(this, listen);
            System.out.println(name + " is now listening to " + listen);
        } else if (type.equals(UNLISTEN)) {
            DataInputStream din = new DataInputStream(new ByteArrayInputStream(messageData));
            String listen = din.readUTF();
            din.close();

            server.unlisten(this, listen);
            System.out.println(name + " is no longer listening to " + listen);
        } else {
            server.dispatchMessage(new Message(type, messageData));
        }

        i.close();
    }

    private void flushMessages(DataOutputStream out) throws IOException{
        Message m;
        while ((m = incoming.poll()) != null) {
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream d = new DataOutputStream(b);

            d.writeUTF(m.getType());
            d.writeInt(m.getData().length);
            d.write(m.getData());

            byte[] message = b.toByteArray();
            out.writeInt(message.length);
            out.write(message);
        }
    }
    
    @Override
    public void run() {
        try {
            DataInputStream in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            System.out.println("Client connected");
            
            boolean connected = true;
            while (connected) {
                while (in.available() > 0) {
                    if (!identified) {
                        String name = in.readUTF();
                        loadName(name);
                        identified = true;
                        continue;
                    }
                    
                    readMessage(in);
                }

                flushMessages(out);
                
                timer--;
                if (timer == 0) {
                    System.out.println(name + " timed out");
                    connected = false;
                }
                
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            System.err.println("Exception in client connection:");
            e.printStackTrace();
        }

        server.unlistenAll(this);
    }
}
