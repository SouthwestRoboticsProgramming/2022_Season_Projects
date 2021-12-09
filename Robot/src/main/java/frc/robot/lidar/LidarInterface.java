package frc.robot.lidar;

import frc.robot.taskmanager.client.Coprocessor;
import frc.robot.taskmanager.client.Task;

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
        task.sendMessage(START);
    }

    public void stopScan() {
        task.sendMessage(STOP);
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
                int angle = in.readDouble();
                int distance = in.readDouble();
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