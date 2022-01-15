package frc.shufflewood;

import frc.messenger.client.MessengerClient;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

public class MessengerAccess {
    private final MessengerClient msg;
    private final Map<String, Set<BiConsumer<String, byte[]>>> listeners;

    public MessengerAccess(MessengerClient msg) {
        this.msg = msg;
        msg.setCallback(this::dispatchMessage);
        listeners = new HashMap<>();
    }

    public void listen(String message, BiConsumer<String, byte[]> listener) {
        Set<BiConsumer<String, byte[]>> set;
        if (listeners.containsKey(message)) {
            set = listeners.get(message);
        } else {
            msg.listen(message);
            set = new HashSet<>();
            listeners.put(message, set);
        }
        set.add(listener);
    }

    public void sendMessage(String message, byte[] data) {
        msg.sendMessage(message, data);
    }

    public void read() {
        msg.read();
    }

    private void dispatchMessage(String message, byte[] data) {
        if (!listeners.containsKey(message)) return;
        for (BiConsumer<String, byte[]> listener : listeners.get(message)) {
            listener.accept(message, data);
        }
    }
}
