package frc.taskmanager;

import frc.messenger.client.MessengerClient;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class TaskManager {
    public final MessengerClient msg;
    public final String messagePrefix;
    private final Map<String, Task> tasks;
    private final File taskFolder;

    public TaskManager(String host, int port, File taskFolder, String messagePrefix) {
        this.messagePrefix = messagePrefix;
        System.out.println("Connecting to Messenger server at " + host + ":" + port);
        msg = new MessengerClient(host, port, messagePrefix);
        msg.listen(messagePrefix + Messages.START_TASK);
        msg.listen(messagePrefix + Messages.STOP_TASK);
        msg.listen(messagePrefix + Messages.DELETE_TASK);
        msg.listen(messagePrefix + Messages.UPLOAD_TASK);
        msg.listen(messagePrefix + Messages.GET_TASKS);
        msg.listen(messagePrefix + Messages.IS_TASK_RUNNING);
        msg.setCallback(this::messageCallback);

        System.out.println("Loading tasks");
        tasks = new HashMap<>();
        for (File file : taskFolder.listFiles()) {
            if (!file.isDirectory()) {
                System.out.println("Warning: Unexpected file '" + file.getName() + "' in task folder");
                continue;
            }

            String name = file.getName();
            tasks.put(name, new Task(name, file, this));
            System.out.println("Loaded task '" + name + "'");
        }
        this.taskFolder = taskFolder;

        System.out.println("Ready.");
    }

    public MessengerClient getMessenger() {
        return msg;
    }

    private void handleStart(byte[] data) {
        String taskName = decodeString(data);
        Task task = tasks.get(taskName);
        if (task == null) {
            System.out.println("Warning: Can't start nonexistent task '" + taskName + "'");
            return;
        }

        task.start();
    }

    private void handleStop(byte[] data) {
        String taskName = decodeString(data);
        Task task = tasks.get(taskName);
        if (task == null) {
            System.out.println("Warning: Can't stop nonexistent task '" + taskName + "'");
            return;
        }

        task.stop();
    }

    private void handleDelete(byte[] data) {
        String taskName = decodeString(data);
        Task task = tasks.get(taskName);
        if (task == null) {
            System.out.println("Warning: Can't delete nonexistent task '" + taskName + "'");
            return;
        }

        task.delete();
        tasks.remove(taskName);
    }

    private void handleUpload(byte[] data) {
        ByteArrayInputStream b = new ByteArrayInputStream(data);
        DataInputStream d = new DataInputStream(b);

        String task;
        byte[] payload;
        try {
            task = d.readUTF();
            int len = d.readInt();
            payload = new byte[len];
            d.readFully(payload);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // Delete if it already exists
        Task existing = tasks.get(task);
        if (existing != null) {
            existing.delete();
            tasks.remove(task);
        }

        // Create task folder
        File folder = new File(taskFolder, task);

        // Decompress payload as ZIP archive
        try {
            ZipInputStream zip = new ZipInputStream(new ByteArrayInputStream(payload));
            ZipEntry entry;
            while ((entry = zip.getNextEntry()) != null) {
                File file = newFile(folder, entry);
                if (entry.isDirectory()) {
                    if (!file.isDirectory() && !file.mkdirs()) {
                        throw new IOException("Failed to create directory " + file);
                    }
                } else {
                    File parent = file .getParentFile();
                    if (!parent.isDirectory() && !parent.mkdirs()) {
                        throw new IOException("Failed to create directory " + parent);
                    }

                    FileOutputStream fos = new FileOutputStream(file);
                    int length;
                    byte[] buffer = new byte[1024];
                    while ((length = zip.read(buffer)) > 0) {
                        fos.write(buffer, 0, length);
                    }
                    fos.close();
                }
            }
            zip.closeEntry();
            zip.close();

            // Mark the task script as executable
            new File(folder, "task.sh").setExecutable(true);

            System.out.println("Imported task data for '" + task + "'");
            tasks.put(task, new Task(task, folder, this));
        } catch (IOException e) {
            System.err.println("Error while decoding task data:");
            e.printStackTrace();
        }
    }

    private void handleGetTasks() {
        Set<String> tasks = this.tasks.keySet();
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream d = new DataOutputStream(b);

        try {
            d.writeInt(tasks.size());
            for (String str : tasks) {
                d.writeUTF(str);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        msg.sendMessage(messagePrefix + Messages.TASKS_RESPONSE, b.toByteArray());
    }

    private void handleIsRunning(byte[] data) {
        String taskName = decodeString(data);
        Task task = tasks.get(taskName);

        boolean running = false;
        if (task == null) {
            System.out.println("Warning: Cannot get whether nonexistent task '" + taskName + "' is running, returning false");
        } else {
            running = task.isRunning();
        }

        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream d = new DataOutputStream(b);
        try {
            d.writeUTF(taskName);
            d.writeBoolean(running);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        msg.sendMessage(messagePrefix + Messages.RUNNING_RESPONSE, b.toByteArray());
    }

    private File newFile(File destDir, ZipEntry entry) throws IOException {
        File destFile = new File(destDir, entry.getName());

        String destPath = destDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();

        if (!destFilePath.startsWith(destPath + File.separator)) {
            throw new IOException("ZIP entry outside of target directory: " + entry.getName());
        }

        return destFile;
    }

    private void messageCallback(String type, byte[] data) {
        if (type.equals(messagePrefix + Messages.START_TASK)) {
            handleStart(data);
        } else if (type.equals(messagePrefix + Messages.STOP_TASK)) {
            handleStop(data);
        } else if (type.equals(messagePrefix + Messages.DELETE_TASK)) {
            handleDelete(data);
        } else if (type.equals(messagePrefix + Messages.UPLOAD_TASK)) {
            handleUpload(data);
        } else if (type.equals(messagePrefix + Messages.GET_TASKS)) {
            handleGetTasks();
        } else if (type.equals(messagePrefix + Messages.IS_TASK_RUNNING)) {
            handleIsRunning(data);
        } else {
            System.out.println("Unknown message: " + type);
        }
    }

    private String decodeString(byte[] data) {
        ByteArrayInputStream b = new ByteArrayInputStream(data);
        DataInputStream d = new DataInputStream(b);

        String task = "Decode error";
        try {
            task = d.readUTF();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return task;
    }

    public void run() {
        while (true) {
            synchronized (msg) {
                msg.read();
            }

            try {
                Thread.sleep(25);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
