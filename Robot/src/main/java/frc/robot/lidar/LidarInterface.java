package frc.robot.lidar;

import frc.robot.taskmanager.client.Coprocessor;
import frc.robot.taskmanager.client.Task;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public final class LidarInterface {
    private static final String START = "Start";
    private static final String STOP = "Stop";
    private static final String SCAN = "Scan";

    private final Task task;
    private Consumer<List<ScanEntry>> scanCallback = (scan) -> {};

    public LidarInterface(Coprocessor cp, String name) {
        task = cp.getTask(name);
        task.setMessageReceiveCallback(this::onMessage);
        task.start();
    }

    public void startScan() {
        task.sendMessage(START, new byte[0]);
    }

    public void stopScan() {
        task.sendMessage(STOP, new byte[0]);
    }

    public void stop() {
        task.stop();
    }

    public void setScanCallback(Consumer<List<ScanEntry>> callback) {
        scanCallback = callback;
    }

    private void readScan(byte[] data) {
        try {
            ByteArrayInputStream b = new ByteArrayInputStream(data);
            DataInputStream in = new DataInputStream(b);

            List<ScanEntry> scan = new ArrayList<>();
            int count = in.readInt();
            for (int i = 0; i < count; i++) {
                int quality = in.readInt();
                double angle = in.readDouble();
                double distance = in.readDouble();
                scan.add(new ScanEntry(quality, angle, distance));
            }

            scanCallback.accept(scan);

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
            default:
                System.out.println("Warning: Unknown message: " + type);
                break;
        }
    }
}