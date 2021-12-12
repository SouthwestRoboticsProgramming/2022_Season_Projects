package frc.robot.lidar;

import frc.robot.taskmanager.client.Coprocessor;
import frc.robot.taskmanager.client.Task;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.function.Consumer;

public final class LidarInterface {
    private static final String START = "Start";
    private static final String STOP = "Stop";
    private static final String SCAN = "Scan";
    private static final String READY = "Ready";

    private final Task task;
    private Consumer<ScanEntry> scanCallback = (scan) -> {};

    public LidarInterface(Coprocessor cp, String name) {
        task = cp.getTask(name);
        task.setMessageReceiveCallback(this::onMessage);
        task.stop(); // Make sure it is stopped
        try { Thread.sleep(1000); } catch (Throwable e) {}
        task.start(); // then start it

        // Wait for the lidar to be ready
        try {
            wait();
        } catch (Throwable e) {}

        try { Thread.sleep(10000); } catch (Throwable e) {}
        System.out.println("Lidar successfully initialized!");
    }

    public void startScan() {
        System.out.println("Lidar: Sending scan start message");
        task.sendMessage(START, new byte[0]);
    }

    public void stopScan() {
        System.out.println("Lidar: Sending scan stop message");
        task.sendMessage(STOP, new byte[0]);
    }

    public void stop() {
        task.stop();
    }

    public void setScanCallback(Consumer<ScanEntry> callback) {
        scanCallback = callback;
    }

    private void readScan(byte[] data) {
        try {
            ByteArrayInputStream b = new ByteArrayInputStream(data);
            DataInputStream in = new DataInputStream(b);

            int quality = in.readInt();
            double angle = in.readDouble();
            double distance = in.readDouble();

            scanCallback.accept(new ScanEntry(quality, angle, distance));

            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void onMessage(String type, byte[] data) {
        switch (type) {
            case SCAN: 
                readScan(data);
                break;
            case READY:
                System.out.println("Lidar is ready.");
                notify();
                break;
            default:
                System.out.println("Warning: Unknown message: " + type);
                break;
        }
    }
}