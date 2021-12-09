package frc.taskmanager.server;

import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.StartedProcess;
import org.zeroturnaround.exec.stream.LogOutputStream;

import java.io.*;
import java.nio.file.Files;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Task {
    private static final String[] COMMAND = {"bash", "task.sh"};

    private final String name;
    private final File folder;
    private final TaskManager manager;
    private final Queue<TaskboundMessage> messageQueue;
    private Process process;

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
            ProcessExecutor executor = new ProcessExecutor();
            executor.command(COMMAND);
            executor.directory(folder);
            executor.redirectOutput(new LogOutputStream() {
                @Override
                protected void processLine(String s) {
                    System.out.println("out: " + s);

                    ByteArrayOutputStream b = new ByteArrayOutputStream();
                    DataOutputStream d = new DataOutputStream(b);

                    try {
                        d.writeUTF(s);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    manager.queueClientboundMessage(new ClientboundMessage(name, "STDOUT", b.toByteArray()));
                }
            });
            executor.redirectError(new LogOutputStream() {
                @Override
                protected void processLine(String s) {
                    System.err.println("err: " + s);

                    ByteArrayOutputStream b = new ByteArrayOutputStream();
                    DataOutputStream d = new DataOutputStream(b);

                    try {
                        d.writeUTF(s);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    manager.queueClientboundMessage(new ClientboundMessage(name, "STDERR", b.toByteArray()));
                }
            });
            StartedProcess p = executor.start();
            process = p.getProcess();
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
	process.descendants().forEach((child) -> {
		child.destroyForcibly();
	    });
        process.destroyForcibly();
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

    public void queueMessage(TaskboundMessage message) {
        messageQueue.add(message);
    }

    public TaskboundMessage pollMessageQueue() {
        return messageQueue.poll();
    }
}
