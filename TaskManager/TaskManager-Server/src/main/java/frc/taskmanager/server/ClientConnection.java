package frc.taskmanager.server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Collection;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ClientConnection {
    public static final String DEST = "TaskManager";
    public static final String START_TASK = "StartTask";
    public static final String STOP_TASK = "StopTask";
    public static final String DELETE_TASK = "DeleteTask";
    public static final String UPLOAD_TASK = "UploadTask";
    public static final String TASK_EXISTS = "TaskExists";
    public static final String TASK_RUNNING = "TaskRunning";
    public static final String HEARTBEAT = "Heartbeat";
    public static final String GET_TASKS = "GetTasks";

    public static final String ORIGIN = "TaskManager";
    public static final String TASK_EXISTS_RESPONSE = "TaskExistsResponse";
    public static final String TASK_RUNNING_RESPONSE = "TaskRunningResponse";
    public static final String GET_TASKS_RESPONSE = "GetTasksResponse";

    private final int HEARTBEAT_TIMEOUT = 300;

    private final Socket socket;
    private final TaskManager manager;
    private DataInputStream in;
    private DataOutputStream out;
    private int heartbeatTimer;

    public ClientConnection(Socket socket, TaskManager manager) {
        this.socket = socket;
        this.manager = manager;
    }

    private DataInputStream createInputStream(byte[] data) {
        return new DataInputStream(new ByteArrayInputStream(data));
    }

    private String getString(byte[] data) throws IOException {
        DataInputStream i = createInputStream(data);
        return i.readUTF();
    }

    private void handleStartTask(byte[] data) throws IOException {
        Task task = manager.getTask(getString(data));
        if (task == null) {
            return;
        }
        task.start();
    }

    private void handleStopTask(byte[] data) throws IOException {
        Task task = manager.getTask(getString(data));
        if (task == null) {
            return;
        }
        task.stop();
    }

    private void handleDeleteTask(byte[] data) throws IOException {
        Task task = manager.getTask(getString(data));
        if (task == null) {
            System.out.println("Warning: Cannot delete nonexistent task");
            return;
        }
        task.delete();
        manager.removeTask(task);
        System.out.println("Deleted task '" + task.getName() + "'");
    }

    private void handleUploadTask(byte[] data) throws IOException {
        ByteArrayInputStream stream = new ByteArrayInputStream(data);
        DataInputStream dataIn = new DataInputStream(stream);
        String taskName = dataIn.readUTF();

        System.out.println("Receiving data for new task '" + taskName + "'");

        // Make sure the specified task does not exist
        Task existingTask = manager.getTask(taskName);
        if (existingTask != null) {
            System.out.println("Task '" + taskName + "' already exists, deleting it");
            existingTask.delete();
            manager.removeTask(existingTask);
        }

        // Create a folder for the task files to live in
        File taskFolder = new File(manager.getTaskFolder(), taskName);
        taskFolder.mkdir();

        // Treat the task payload as a ZIP archive and decompress it
        int payloadLen = dataIn.readInt();
        byte[] payload = new byte[payloadLen];
        dataIn.readFully(payload);

        ZipInputStream zip = new ZipInputStream(new ByteArrayInputStream(payload));
        ZipEntry entry;
        while ((entry = zip.getNextEntry()) != null) {
            File newFile = newFile(taskFolder, entry);
            if (entry.isDirectory()) {
                if (!newFile.isDirectory() && !newFile.mkdirs()) {
                    throw new IOException("Failed to create directory " + newFile);
                }
            } else {
                File parent = newFile.getParentFile();
                if (!parent.isDirectory() && !parent.mkdirs()) {
                    throw new IOException("Failed to create directory " + parent);
                }

                FileOutputStream fos = new FileOutputStream(newFile);
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

        // Make sure the task script is executable
        new File(taskFolder, "task.sh").setExecutable(true);

        System.out.println("Imported data for new task '" + taskName + "'");

        manager.loadTask(taskName);
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

    private void handleTaskExists(byte[] data) throws IOException {
        String name = getString(data);

        boolean exists = manager.getTask(name) != null;

        writeTaskBooleanPacket(TASK_EXISTS_RESPONSE, name, exists);
    }

    private void handleTaskRunning(byte[] data) throws IOException {
        String name = getString(data);
        Task task = manager.getTask(name);
        boolean running = task != null && task.isRunning();

        writeTaskBooleanPacket(TASK_RUNNING_RESPONSE, name, running);
    }

    private void handleGetTasks() throws IOException {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream d = new DataOutputStream(b);

        Collection<Task> tasks = manager.getTasks();
        d.writeInt(tasks.size());
        for (Task task : tasks) {
            d.writeUTF(task.getName());
        }

        writePacket(ORIGIN, GET_TASKS_RESPONSE, b.toByteArray());
    }

    private void writeTaskBooleanPacket(String type, String task, boolean bool) throws IOException {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream d = new DataOutputStream(b);

        d.writeUTF(task);
        d.writeBoolean(bool);

        writePacket(ORIGIN, type, b.toByteArray());
    }

    private void writePacket(String origin, String type, byte[] data) throws IOException {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream o = new DataOutputStream(b);

        o.writeUTF(origin);
        o.writeUTF(type);
        o.writeInt(data.length);
        o.write(data);

        out.writeInt(b.size());
        out.write(b.toByteArray());
    }

    private void handlePacket(String dest, String type, byte[] data) throws IOException {
        if (dest.equals(DEST)) {
            switch (type) {
                case HEARTBEAT:
                    heartbeatTimer = HEARTBEAT_TIMEOUT;
                    break;
                case START_TASK:
                    handleStartTask(data);
                    break;
                case STOP_TASK:
                    handleStopTask(data);
                    break;
                case DELETE_TASK:
                    handleDeleteTask(data);
                    break;
                case UPLOAD_TASK:
                    handleUploadTask(data);
                    break;
                case TASK_EXISTS:
                    handleTaskExists(data);
                    break;
                case TASK_RUNNING:
                    handleTaskRunning(data);
                    break;
                case GET_TASKS:
                    handleGetTasks();
                    break;
            }
        } else {
            Task task = manager.getTask(dest);
            if (task == null) {
                System.out.println("Warning: Received message for nonexistent task '" + dest + "'");
                return;
            }

            task.queueMessage(new TaskboundMessage(type, data));
        }
    }

    private void readPacket() throws IOException {
        int length = in.readInt();
        byte[] packetData = new byte[length];
        in.readFully(packetData);
        DataInputStream i = createInputStream(packetData);

        String dest = i.readUTF();
        String type = i.readUTF();
        int dataLength = i.readInt();
        byte[] data = new byte[dataLength];
        i.readFully(data);

        handlePacket(dest, type, data);
    }

    private void flushTaskMessages() throws IOException {
        ClientboundMessage msg;
        while ((msg = manager.pollMessageQueue()) != null) {
            writePacket(msg.getOrigin(), msg.getType(), msg.getData());
        }
    }

    public void run() throws IOException {
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());

        heartbeatTimer = HEARTBEAT_TIMEOUT;

        while (heartbeatTimer > 0) {
            heartbeatTimer--;

            while (in.available() > 0) {
                readPacket();
            }

            flushTaskMessages();

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("No heartbeat received in the past " + (HEARTBEAT_TIMEOUT / 100) + " seconds, assuming the connection is dropped");
    }
}
