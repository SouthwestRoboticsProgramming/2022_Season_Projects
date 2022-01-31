package frc.messenger.server;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class MessengerServer {
    private final Map<String, Set<ClientHandlerThread>> listeners;
    private final Queue<Message> messages;

    public MessengerServer() {
        listeners = new ConcurrentHashMap<>();
        messages = new ConcurrentLinkedQueue<>();
    }

    private void flushMessages() {
        Message m;
        while ((m = messages.poll()) != null) {
            String type = m.getType();
            Set<ClientHandlerThread> handlers = listeners.get(type);
            if (handlers == null) {
                continue;
            }

            for (ClientHandlerThread thread : handlers) {
                thread.sendMessage(m);
            }
        }
    }

    public void dispatchMessage(Message message) {
        messages.add(message);
    }

    public void listen(ClientHandlerThread thread, String type) {
        Set<ClientHandlerThread> set = listeners.computeIfAbsent(type, (k) -> Collections.synchronizedSet(new HashSet<>()));
        set.add(thread);
    }

    public void unlisten(ClientHandlerThread thread, String type) {
        Set<ClientHandlerThread> set = listeners.get(type);
        if (set == null) return;
        set.remove(thread);
    }

    public void unlistenAll(ClientHandlerThread thread) {
        for (Map.Entry<String, Set<ClientHandlerThread>> entry : listeners.entrySet()) {
            entry.getValue().remove(thread);
        }
    }

    public void run() {
        MessengerConfig config = MessengerConfig.loadFromFile(new File("config.properties"));

        new ClientConnectThread(this, config.getPort()).start();

        while (true) {
            flushMessages();

            try {
                Thread.sleep(25);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
