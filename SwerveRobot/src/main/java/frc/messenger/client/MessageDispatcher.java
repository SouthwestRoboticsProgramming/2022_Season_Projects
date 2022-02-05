package frc.messenger.client;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MessageDispatcher {
    private final MessengerClient msg;
    private final Map<String, Set<MessageHandler>> listeners;

    public MessageDispatcher(MessengerClient msg) {
        this.msg = msg;
        listeners = new HashMap<>();

        msg.setCallback(this::onMessage);
    }

    public void sendMessage(String type, byte[] data) {
        msg.sendMessage(type, data);
    }

    public void addMessageHandler(MessageHandler handler) {
        for (String type : handler.getListening()) {
            Set<MessageHandler> handlers = listeners.computeIfAbsent(type, (t) -> new HashSet<>());
            if (handlers.isEmpty()) {
                msg.listen(type);
            }
            handlers.add(handler);
        }
    }

    public void removeMessageHandler(MessageHandler handler) {
        for (String type : handler.getListening()) {
            Set<MessageHandler> handlers = listeners.get(type);
            if (handlers == null) continue;

            handlers.remove(handler);
            if (handlers.isEmpty()) {
                msg.unlisten(type);
            }
        }
    }

    private void onMessage(String type, byte[] data) {
        Set<MessageHandler> handlers = listeners.get(type);
        if (handlers == null || handlers.isEmpty()) return;

        for (MessageHandler handler : handlers) {
            handler.onMessage(type, data);
        }
    }
}
