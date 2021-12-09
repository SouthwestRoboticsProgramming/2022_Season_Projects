package frc.taskmanager.client;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

/**
 * Allows interfacing with a task running on a coprocessor.
 * Methods that require a response from the coprocessor will
 * return a future that will be completed when the response
 * is received.
 *
 * @author rmheuer
 */
public class Task {
    private static BiConsumer<Task, String> defaultStdOutCallback = (task, line) -> System.out.println("[" + task.name + "/OUT]: " + line);
    private static BiConsumer<Task, String> defaultStdErrCallback = (task, line) -> System.err.println("[" + task.name + "/ERR]: " + line);

    public static void setDefaultStdOutCallback(BiConsumer<Task, String> defaultStdOutCallback) {
        Task.defaultStdOutCallback = defaultStdOutCallback;
    }

    public static void setDefaultStdErrCallback(BiConsumer<Task, String> defaultStdErrCallback) {
        Task.defaultStdErrCallback = defaultStdErrCallback;
    }

    private final Coprocessor cp;
    private final String name;
    private BiConsumer<String, byte[]> messageCallback;
    private BiConsumer<Task, String> stdOutCallback;
    private BiConsumer<Task, String> stdErrCallback;
    private final Set<CompletableFuture<Boolean>> existsFutures;
    private final Set<CompletableFuture<Boolean>> runningFutures;

    /**
     * Do not call. For internal use only.
     *
     * @param cp The coprocessor this task belongs to.
     * @param name The name of this task.
     */
    Task(Coprocessor cp, String name) {
        this.cp = cp;
        this.name = name;
        messageCallback = (n, d) -> {};
        existsFutures = new HashSet<>();
        runningFutures = new HashSet<>();
    }

    /**
     * Gets the name of this task. This name corresponds to the
     * folder in which the task data is stored on the coprocessor.
     *
     * @return The name of this task.
     */
    public String getName() {
        return name;
    }

    /**
     * Signals the coprocessor to start executing this task.
     */
    public void start() {
        sendNamedMessage(Coprocessor.START_TASK);
    }

    /**
     * Signals the coprocessor to stop executing this task. If the
     * task is not running, this will be ignored with a warning in
     * the manager log.
     */
    public void stop() {
        sendNamedMessage(Coprocessor.STOP_TASK);
    }

    /**
     * Gets whether this task is currently running. This method
     * requires the coprocessor to send a response, and will
     * return a CompletableFuture of the result which is completed
     * when the result is received.
     *
     * @return A future of whether this task is running.
     */
    public CompletableFuture<Boolean> isRunning() {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        runningFutures.add(future);
        sendNamedMessage(Coprocessor.TASK_RUNNING);
        return future;
    }

    /**
     * Sends a message to the task.
     *
     * @param type The type of message. This can be any string.
     * @param data Arbitrary data that can be sent along with
     *             the message.
     */
    public void sendMessage(String type, byte[] data) {
        try {
            cp.sendMessage(name, type, data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets a callback that is called when a message is received
     * from the task running on the coprocessor.
     *
     * @param callback Function to call when a message is received.
     */
    public void setMessageReceiveCallback(BiConsumer<String, byte[]> callback) {
        messageCallback = callback;
    }

    /**
     * Sets a callback that is called when a line is received from
     * the standard output of the task.
     *
     * @param callback Function to call when a line is received.
     */
    public void setStdOutCallback(BiConsumer<Task, String> callback) {
        stdOutCallback = callback;
    }

    /**
     * Sets a callback that is called when a line is received from
     * the standard error output of the task.
     *
     * @param callback Function to call when a line is received.
     */
    public void setStdErrCallback(BiConsumer<Task, String> callback) {
        stdErrCallback = callback;
    }

    /**
     * Gets whether this task exists on the coprocessor. This
     * method requires the coprocessor to send a response, and will
     * return a CompletableFuture of the result which is completed
     * when the result is received.
     *
     * @return A future of whether this task exists.
     */
    public CompletableFuture<Boolean> exists() {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        existsFutures.add(future);
        sendNamedMessage(Coprocessor.TASK_EXISTS);
        return future;
    }

    /**
     * Deletes this task from the coprocessor.
     */
    public void delete() {
        sendNamedMessage(Coprocessor.DELETE_TASK);
    }

    /**
     * Uploads a byte array as task data to execute. This data
     * should be a ZIP archive, containing all of the task files
     * at the root. A task must contain a "task.sh" file to be
     * able to run it.
     *
     * @param data byte array of ZIP archive data
     */
    public void upload(byte[] data) {
        try {
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream d = new DataOutputStream(b);

            d.writeUTF(name);
            d.writeInt(data.length);
            d.write(data);

            cp.sendMessage(Coprocessor.DEST, Coprocessor.UPLOAD_TASK, b.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void onMessage(String type, byte[] data) {
        messageCallback.accept(type, data);
    }

    void onStdOut(String line) {
        if (stdOutCallback == null) {
            defaultStdOutCallback.accept(this, line);
        } else {
            stdOutCallback.accept(this, line);
        }
    }

    void onStdErr(String line) {
        if(stdErrCallback == null) {
            defaultStdErrCallback.accept(this, line);
        } else {
            stdErrCallback.accept(this, line);
        }
    }

    void onExistsResponse(boolean exists) {
        for (CompletableFuture<Boolean> future : existsFutures) {
            future.complete(exists);
        }
        existsFutures.clear();
    }

    void onRunningResponse(boolean running) {
        for (CompletableFuture<Boolean> future : runningFutures) {
            future.complete(running);
        }
        runningFutures.clear();
    }

    private void sendNamedMessage(String type) {
        try {
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream d = new DataOutputStream(b);

            d.writeUTF(name);

            cp.sendMessage(Coprocessor.DEST, type, b.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
