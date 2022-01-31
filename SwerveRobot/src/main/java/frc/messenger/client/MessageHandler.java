package frc.messenger.client;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class MessageHandler {
    private final Set<String> listening;
    private MessageHandlerFunc handler = (t, s) -> {};

    public MessageHandler() {
        listening = new HashSet<>();
    }

    public MessageHandler listen(String type) {
        listening.add(type);
        return this;
    }

    public MessageHandler setHandler(MessageHandlerFunc handler) {
        this.handler = handler;
        return this;
    }

    public void onMessage(String type, byte[] data) {
        ByteArrayInputStream b = new ByteArrayInputStream(data);
        DataInputStream in = new DataInputStream(b);

        try {
            handler.handleMessage(type, in);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Set<String> getListening() {
        return new HashSet<>(listening);
    }
}
