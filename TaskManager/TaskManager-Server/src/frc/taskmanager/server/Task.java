package frc.taskmanager.server;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Task {
    private static final String COMMAND = "./task.sh";

    private final String name;
    private final File folder;
    private final TaskManager manager;
    private final Queue<TaskboundMessage> messageQueue;
    private Process process;
    private BufferedReader stdOut;
    private BufferedReader stdErr;

    public Task(String name, File folder, TaskManager manager) {
        this.name = name;
        this.folder = folder;
        this.manager = manager;
        this.messageQueue = new ConcurrentLinkedQueue<>();
    }

    public String getName() {
        return name;
    }

    public void start() {
        if (isRunning()) {
            System.out.println("Warning: Received request to start task '" + name + "' but it is already running");
            return;
        }
        System.out.println("Starting task '" + name + "'");
        try {
            process = Runtime.getRuntime().exec(COMMAND, new String[0], folder);
            stdOut = new BufferedReader(new InputStreamReader(process.getInputStream()));
            stdErr = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        } catch (Exception e) {
            System.err.println("Error while starting task '" + name + "':");
            e.printStackTrace();
        }
    }

    public void stop() {
        if (!isRunning()) {
            System.out.println("Warning: Received request to stop task '" + name + "' but it is not running");
            return;
        }

        System.out.println("Stopping task '" + name + "'");
        process.destroy();
    }

    public void delete() {
        if (isRunning()) {
            stop();
        }
        deleteFile(folder);
    }

    // From https://stackoverflow.com/a/29175213
    private void deleteFile(File file) {
        File[] contents = file.listFiles();
        if (contents != null) {
            for (File f : contents) {
                if (!Files.isSymbolicLink(f.toPath())) {
                    deleteFile(f);
                }
            }
        }
        file.delete();
    }

    public boolean isRunning() {
        return process != null && process.isAlive();
    }

    public void flushOutput() {
        if (!isRunning()) {
            return;
        }

        try {
            while (stdOut.ready()) {
                String line = stdOut.readLine();
                ByteArrayOutputStream b = new ByteArrayOutputStream();
                DataOutputStream o = new DataOutputStream(b);
                o.writeUTF(line);
                o.close();

                manager.queueClientboundMessage(new ClientboundMessage(name, "STDOUT", b.toByteArray()));
            }
        } catch (IOException e) {
            System.err.println("Exception while flushing stdout:");
            e.printStackTrace();
        }

        try {
            while (stdErr.ready()) {
                String line = stdErr.readLine();
                ByteArrayOutputStream b = new ByteArrayOutputStream();
                DataOutputStream o = new DataOutputStream(b);
                o.writeUTF(line);
                o.close();

                manager.queueClientboundMessage(new ClientboundMessage(name, "STDERR", b.toByteArray()));
            }
        } catch (IOException e) {
            System.err.println("Exception while flushing stderr:");
            e.printStackTrace();
        }
    }

    public void queueMessage(TaskboundMessage message) {
        messageQueue.add(message);
    }

    public TaskboundMessage pollMessageQueue() {
        return messageQueue.poll();
    }
}
