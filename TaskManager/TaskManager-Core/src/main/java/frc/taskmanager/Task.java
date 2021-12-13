package frc.taskmanager;

import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.StartedProcess;
import org.zeroturnaround.exec.stream.LogOutputStream;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Task {
    private static final String[] COMMAND = {"bash", "task.sh"};

    private final String name;
    private final File folder;
    private final TaskManager manager;
    private Process process;

    public Task(String name, File folder, TaskManager manager) {
        this.name = name;
        this.folder = folder;
        this.manager = manager;
    }

    public String getName() {
        return name;
    }

    public void start() {
        if (isRunning()) {
            stop();
        }

        System.out.println("Starting task '" + name + "'");
        try {
            StartedProcess p = new ProcessExecutor()
                    .command(COMMAND)
                    .directory(folder)
                    .redirectOutput(new OutputLogger(manager.messagePrefix + Messages.STDOUT + name))
                    .redirectError(new OutputLogger(manager.messagePrefix + Messages.STDERR + name))
                    .start();
            process = p.getProcess();
        } catch (IOException e) {
            System.err.println("Error while starting task '" + name + "':");
            e.printStackTrace();
        }
    }

    public void stop() {
        if (!isRunning()) {
            return;
        }

        System.out.println("Stopping task '" + name + "'");
        process.descendants().forEach((child) -> {
            child.destroyForcibly(); // Don't take this line out of context
        });
        process.destroyForcibly();
    }

    public void delete() {
        System.out.println("Deleting task '" + name + "'");

        if (isRunning()) {
            stop();
        }
        deleteFile(folder);
    }

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

    private class OutputLogger extends LogOutputStream {
        private final String type;

        private OutputLogger(String type) {
            this.type = type;
        }

        @Override
        protected void processLine(String s) {
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream d = new DataOutputStream(b);

            try {
                d.writeUTF(s);
            } catch (IOException e) {
                e.printStackTrace();
            }

            synchronized (manager.msg) {
                manager.msg.sendMessage(type, b.toByteArray());
            }
        }
    }
}
