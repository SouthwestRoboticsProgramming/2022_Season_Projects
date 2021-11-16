package frc.taskmanager.server;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
    private InputStream stdOutInput;
    private InputStream stdErrInput;
    private LogOutputStream stdOutOutput;
    private LogOutputStream stdErrOutput;

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
            stdOutInput = process.getInputStream();
            stdErrInput = process.getErrorStream();
            
            stdOutOutput = new LogOutputStream() {
                @Override
                protected void processLine(String line) {
                    ByteArrayOutputStream b = new ByteArrayOutputStream();
                    try {
                        DataOutputStream o = new DataOutputStream(b);
                        o.writeUTF(line);
                        o.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        return;
                    }
                    
                    manager.queueClientboundMessage(new ClientboundMessage(name, "STDOUT", b.toByteArray()));
                }
            };
            stdErrOutput = new LogOutputStream() {
                @Override
                protected void processLine(String line) {
                    ByteArrayOutputStream b = new ByteArrayOutputStream();
                    try {
                        DataOutputStream o = new DataOutputStream(b);
                        o.writeUTF(line);
                        o.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        return;
                    }
                    
                    manager.queueClientboundMessage(new ClientboundMessage(name, "STDERR", b.toByteArray()));
                }
            };
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
            copyStream(stdOutInput, stdOutOutput);
        } catch (IOException e) {
            System.err.println("Exception while flushing stdout:");
            e.printStackTrace();
        }

        try {
            copyStream(stdErrInput, stdErrOutput);
        } catch (IOException e) {
            System.err.println("Exception while flushing stderr:");
            e.printStackTrace();
        }
    }
    
    private void copyStream(InputStream in, OutputStream out) throws IOException {
        // Don't do anything if there is nothing to read
        if (in.available() == 0) {
            return;
        }

        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }

    public void queueMessage(TaskboundMessage message) {
        messageQueue.add(message);
    }

    public TaskboundMessage pollMessageQueue() {
        return messageQueue.poll();
    }
}