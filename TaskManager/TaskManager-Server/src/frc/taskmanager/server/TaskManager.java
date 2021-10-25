package frc.taskmanager.server;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TaskManager {
    private final File taskFolder;
    private final Map<String, Task> tasks;
    private final Queue<ClientboundMessage> messageQueue;

    public TaskManager(File taskFolder) {
        this.taskFolder = taskFolder;
        tasks = new HashMap<>();
        messageQueue = new ConcurrentLinkedQueue<>();

        for (File file : taskFolder.listFiles()) {
            if (!file.isDirectory()) {
                System.out.println("Warning: Unexpected file '" + file.getName() + "' in task folder");
                continue;
            }

            String name = file.getName();
            if (name.equals("TaskManager")) {
                System.out.println("Warning: Name 'TaskManager' is not allowed for task");
                continue;
            }

            System.out.println("Discovered task '" + file.getName() + "'");
            tasks.put(name, new Task(name, file, this));
        }
    }

    public Task getTask(String name) {
        return tasks.get(name);
    }

    public Iterable<Task> getTasks() {
        return tasks.values();
    }

    public void removeTask(Task task) {
        tasks.remove(task.getName());
        System.out.println("Unloaded task '" + task.getName() + "'");
    }

    public void queueClientboundMessage(ClientboundMessage message) {
        messageQueue.add(message);
    }

    public ClientboundMessage pollMessageQueue() {
        return messageQueue.poll();
    }

    public File getTaskFolder() {
        return taskFolder;
    }

    public void loadTask(String name) {
        tasks.put(name, new Task(name, new File(taskFolder, name), this));
        System.out.println("Loaded task '" + name + "'");
    }
}
