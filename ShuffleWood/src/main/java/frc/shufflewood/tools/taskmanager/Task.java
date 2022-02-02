package frc.shufflewood.tools.taskmanager;

import frc.messenger.client.MessengerClient;
import frc.shufflewood.MessengerAccess;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class Task {
    private final String name;
    private final MessengerAccess msg;
    private final Set<CompletableFuture<Boolean>> runningFutures;
    private final String prefix;

    public Task(String name, MessengerAccess msg, String prefix) {
        this.name = name;
        this.msg = msg;
        this.prefix = prefix;

        runningFutures = new HashSet<>();
    }

    public void start() {
        msg.sendMessage(prefix + Messages.START_TASK, encodeString(name));
    }

    public void stop() {
        msg.sendMessage(prefix + Messages.STOP_TASK, encodeString(name));
    }

    public void delete() {
        msg.sendMessage(prefix + Messages.DELETE_TASK, encodeString(name));
    }

    public void upload(byte[] payload) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream d = new DataOutputStream(b);

        try {
            d.writeUTF(name);
            d.writeInt(payload.length);
            d.write(payload);
        } catch (IOException e) {
            e.printStackTrace();
        }

        msg.sendMessage(prefix + Messages.UPLOAD_TASK, b.toByteArray());
    }

    public CompletableFuture<Boolean> isRunning() {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        runningFutures.add(future);
        msg.sendMessage(prefix + Messages.IS_TASK_RUNNING, encodeString(name));
        return future;
    }

    public String getName() {
        return name;
    }

    public void completeRunning(boolean running) {
        for (CompletableFuture<Boolean> future : runningFutures) {
            future.complete(running);
        }
        runningFutures.clear();
    }

    private byte[] encodeString(String str) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream d = new DataOutputStream(b);

        try {
            d.writeUTF(str);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return b.toByteArray();
    }
}
