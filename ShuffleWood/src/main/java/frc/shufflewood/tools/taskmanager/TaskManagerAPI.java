package frc.shufflewood.tools.taskmanager;

import frc.shufflewood.MessengerAccess;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class TaskManagerAPI {
    private final MessengerAccess msg;
    private final Map<String, Task> tasks;
    private final Set<CompletableFuture<Set<Task>>> tasksFutures;
    private final String prefix;
    private BiConsumer<Task, String> stdOutCallback = (t, s) -> {};
    private BiConsumer<Task, String> stdErrCallback = (t, s) -> {};

    public TaskManagerAPI(MessengerAccess msg, String prefix) {
        this.msg = msg;
        this.prefix = prefix;
        tasks = new HashMap<>();
        tasksFutures = new HashSet<>();
        msg.listen(prefix + Messages.TASKS_RESPONSE, this::messageCallback);
        msg.listen(prefix + Messages.RUNNING_RESPONSE, this::messageCallback);
    }

    public Task getTask(String name) {
        return tasks.computeIfAbsent(name, (n) -> {
            msg.listen(prefix + Messages.STDOUT + n, this::messageCallback);
            msg.listen(prefix + Messages.STDERR + n, this::messageCallback);
            return new Task(n, msg, prefix);
        });
    }

    public void setStdOutCallback(BiConsumer<Task, String> callback) {
        stdOutCallback = callback;
    }

    public void setStdErrCallback(BiConsumer<Task, String> callback) {
        stdErrCallback = callback;
    }

    private void messageCallback(String type, byte[] data) {
        if (type.equals(prefix + Messages.RUNNING_RESPONSE)) {
            DataInputStream in = new DataInputStream(new ByteArrayInputStream(data));
            try {
                String task = in.readUTF();
                getTask(task).completeRunning(in.readBoolean());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (type.equals(prefix + Messages.TASKS_RESPONSE)) {
            DataInputStream in = new DataInputStream(new ByteArrayInputStream(data));
            Set<Task> tasks = new HashSet<>();
            try {
                int count = in.readInt();
                for (int i = 0; i < count; i++) {
                    tasks.add(getTask(in.readUTF()));
                }
                for (CompletableFuture<Set<Task>> future : tasksFutures) {
                    future.complete(tasks);
                }
                tasksFutures.clear();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (type.startsWith(prefix + Messages.STDOUT)) {
            int firstColon = type.indexOf(':');
            int second = type.indexOf(':', firstColon + 1);
            String name = type.substring(second + 1);

            DataInputStream in = new DataInputStream(new ByteArrayInputStream(data));
            try {
                String message = in.readUTF();
                stdOutCallback.accept(getTask(name), message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (type.startsWith(prefix + Messages.STDERR)) {
            int firstColon = type.indexOf(':');
            int second = type.indexOf(':', firstColon + 1);
            String name = type.substring(second + 1);

            DataInputStream in = new DataInputStream(new ByteArrayInputStream(data));
            try {
                String message = in.readUTF();
                stdErrCallback.accept(getTask(name), message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public CompletableFuture<Set<Task>> getAllTasks() {
        CompletableFuture<Set<Task>> future = new CompletableFuture<>();
        tasksFutures.add(future);
        msg.sendMessage(prefix + Messages.GET_TASKS, new byte[0]);
        return future;
    }
}
