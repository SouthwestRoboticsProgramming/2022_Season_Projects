package frc.taskmanager;

import frc.messenger.client.MessengerClient;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class TaskManager {
    private static final String START_TASK = "TaskManager:Start";
    private static final String STOP_TASK = "TaskManager:Stop";
    private final MessengerClient msg;

    public TaskManager(String host, int port) {
        System.out.println("Connecting to Messenger server at " + host + ":" + port);
        msg = new MessengerClient(host, port, "TaskManager");
        msg.listen(START_TASK);
        msg.listen(STOP_TASK);
        msg.setCallback(this::messageCallback);
        System.out.println("Ready.");
    }

    private void handleStart(String task) {

    }

    private void handleStop(String task) {

    }

    private void messageCallback(String type, byte[] data) {
        boolean isStart = type.equals(START_TASK);
        boolean isStop = type.equals(STOP_TASK);
        if (!isStart && !isStop) {
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
        } else {
            handleStop(task);
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
