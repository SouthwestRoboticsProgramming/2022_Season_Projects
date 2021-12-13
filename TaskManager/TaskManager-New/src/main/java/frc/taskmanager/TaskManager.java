package frc.taskmanager;

import frc.messenger.client.MessengerClient;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class TaskManager {
    private final MessengerClient msg;

    public TaskManager(String host, int port) {
        System.out.println("Connecting to Messenger server at " + host + ":" + port);
        msg = new MessengerClient(host, port, "TaskManager");
        msg.listen(Messages.START_TASK);
        msg.listen(Messages.STOP_TASK);
        msg.listen(Messages.DELETE_TASK);
        msg.listen(Messages.UPLOAD_TASK);
        msg.setCallback(this::messageCallback);
        System.out.println("Ready.");
    }

    public MessengerClient getMessenger() {
        return msg;
    }

    private void handleStart(String task) {

    }

    private void handleStop(String task) {

    }

    private void handleDelete(String task) {

    }

    private void handleUpload(String task, byte[] payload) {

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
            msg.read();

            try {
                Thread.sleep(25);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
