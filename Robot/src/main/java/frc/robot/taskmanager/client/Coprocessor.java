package frc.robot.taskmanager.client;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * Represents a connection to an external coprocessor.
 * This class handles connections and communication
 * with the coprocessor, as well as access to the
 * tasks that run on it. The {@code flushNetwork()}
 * method must be called at least once every 10 seconds,
 * or the connection will time out and no messages
 * will be received.
 *
 * @author rmheuer
 */
public class Coprocessor {
    // TODO: Do these need to be public?
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

    private final String host;
    private final int port;
    private final Map<String, Task> taskCache;
    private final Set<CompletableFuture<Set<Task>>> getTasksFutures;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    /**
     * Creates a new instance of this class with a given
     * host and port to connect to. No actual connection is
     * performed in this constructor, and is instead
     * performed in {@code connect()}.
     *
     * @param host host to connect to
     * @param port port on the host to connect to
     */
    public Coprocessor(String host, int port) {
        this.host = host;
        this.port = port;
        taskCache = new HashMap<>();
        getTasksFutures = new HashSet<>();
    }

    /**
     * Attempts to connect to the remote coprocessor at
     * the address provided in the constructor.
     *
     * @throws RuntimeException if the connection failed
     */
    public void connect() {
        try {
            socket = new Socket(host, port);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
        } catch (Exception e) {
            throw new RuntimeException("Failed to connect to coprocessor at " + host + ":" + port + ". Make sure it is turned on and that the TaskManager server is running on the correct port.", e);
        }
    }

    /**
     * Flushes the network queues. This method must be
     * called at least once every 10 seconds, or the
     * coprocessor may time out.
     */
    public void flushNetwork() {
        try {
            while (in.available() > 0) {
                readPacket();
            }

            heartBeat();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Disconnects from the remote coprocessor. Do not
     * call any other methods on this class after
     * disconnecting, as it may throw an exception,
     * and will not actually perform any operations on
     * the remote coprocessor.
     */
    public void disconnect() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Task createTask(String name) {
        return new Task(this, name);
    }

    /**
     * Gets a task by name. It does not matter if the
     * task actually exists on the coprocessor. You can
     * check if it exists using {@link Task#exists()}.
     * This method will always return the same instance
     * for each unique name.
     *
     * @param name task name
     * @return the task with the given name
     */
    public Task getTask(String name) {
        return taskCache.computeIfAbsent(name, this::createTask);
    }

    /**
     * Gets a set of the tasks that exist on the remote
     * coprocessor. This operation requires a response
     * from the coprocessor, and thus returns a future
     * for the result.
     *
     * @return a future for the set of existent tasks
     */
    public CompletableFuture<Set<Task>> getAllTasks() {
        CompletableFuture<Set<Task>> future = new CompletableFuture<>();
        getTasksFutures.add(future);
        try {
            sendMessage(DEST, GET_TASKS, new byte[0]);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return future;
    }

    void sendMessage(String destination, String type, byte[] data) throws IOException {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream o = new DataOutputStream(b);

        o.writeUTF(destination);
        o.writeUTF(type);
        o.writeInt(data.length);
        o.write(data);

        out.writeInt(b.size());
        out.write(b.toByteArray());
    }

    private void readTaskExistsResponse(byte[] data) throws IOException {
        DataInputStream in = createInputStream(data);
        Task task = getTask(in.readUTF());
        boolean exists = in.readBoolean();

        task.onExistsResponse(exists);
    }

    private void readTaskRunningResponse(byte[] data) throws IOException {
        DataInputStream in = createInputStream(data);
        Task task = getTask(in.readUTF());
        boolean running = in.readBoolean();

        task.onRunningResponse(running);
    }

    private void readGetTasksResponse(byte[] data) throws IOException {
        DataInputStream in = createInputStream(data);

        Set<Task> tasks = new HashSet<>();
        int count = in.readInt();
        for (int i = 0; i < count; i++) {
            tasks.add(getTask(in.readUTF()));
        }

        for (CompletableFuture<Set<Task>> future : getTasksFutures) {
            future.complete(new HashSet<>(tasks));
        }
        getTasksFutures.clear();;
    }

    private void readPacket() throws IOException {
        int length = in.readInt();
        byte[] packetData = new byte[length];
        in.readFully(packetData);

        DataInputStream i = createInputStream(packetData);

        String origin = i.readUTF();
        String type = i.readUTF();
        int dataLen = i.readInt();
        byte[] data = new byte[dataLen];
        i.readFully(data);

        if (origin.equals(ORIGIN)) {
            switch (type) {
                case TASK_EXISTS_RESPONSE:
                    readTaskExistsResponse(data);
                    break;
                case TASK_RUNNING_RESPONSE:
                    readTaskRunningResponse(data);
                    break;
                case GET_TASKS_RESPONSE:
                    readGetTasksResponse(data);
                    break;
            }
        } else {
            Task originTask = getTask(origin);

            switch (type) {
                case "STDOUT":
                    originTask.onStdOut(createInputStream(data).readUTF());
                    break;
                case "STDERR":
                    originTask.onStdErr(createInputStream(data).readUTF());
                    break;
                default:
                    originTask.onMessage(type, data);
                    break;
            }
        }
    }

    private void heartBeat() throws IOException {
        sendMessage(DEST, HEARTBEAT, new byte[0]);
    }

    private DataInputStream createInputStream(byte[] data) {
        ByteArrayInputStream b = new ByteArrayInputStream(data);
        return new DataInputStream(b);
    }
}
