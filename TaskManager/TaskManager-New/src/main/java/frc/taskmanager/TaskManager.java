package frc.taskmanager;

import frc.messenger.client.MessengerClient;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class TaskManager {
    public final MessengerClient msg;
    private final Map<String, Task> tasks;
    private final File taskFolder;

    public TaskManager(String host, int port, File taskFolder) {
        System.out.println("Connecting to Messenger server at " + host + ":" + port);
        msg = new MessengerClient(host, port, "TaskManager");
        msg.listen(Messages.START_TASK);
        msg.listen(Messages.STOP_TASK);
        msg.listen(Messages.DELETE_TASK);
        msg.listen(Messages.UPLOAD_TASK);
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

    private void handleStart(String taskName) {
        Task task = tasks.get(taskName);
        if (task == null) {
            System.out.println("Warning: Can't start nonexistent task '" + taskName + "'");
            return;
        }

        task.start();
    }

    private void handleStop(String taskName) {
        Task task = tasks.get(taskName);
        if (task == null) {
            System.out.println("Warning: Can't stop nonexistent task '" + taskName + "'");
            return;
        }

        task.stop();
    }

    private void handleDelete(String taskName) {
        Task task = tasks.get(taskName);
        if (task == null) {
            System.out.println("Warning: Can't delete nonexistent task '" + taskName + "'");
            return;
        }

        task.delete();
        tasks.remove(taskName);
    }

    private void handleUpload(String task, byte[] payload) {
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
        boolean isStart = type.equals(Messages.START_TASK);
        boolean isStop = type.equals(Messages.STOP_TASK);
        boolean isDelete = type.equals(Messages.DELETE_TASK);
        boolean isUpload = type.equals(Messages.UPLOAD_TASK);
        if (!isStart && !isStop && !isDelete && !isUpload) {
            return;
        }

        ByteArrayInputStream b = new ByteArrayInputStream(data);
        DataInputStream d = new DataInputStream(b);

        String task;
        try {
            task = d.readUTF();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        if (isStart) {
            handleStart(task);
        } else if (isStop) {
            handleStop(task);
        } else if (isDelete) {
            handleDelete(task);
        } else {
            byte[] payload;
            try {
                int len = d.readInt();
                payload = new byte[len];
                d.readFully(payload);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }

            handleUpload(task, payload);
        }
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
