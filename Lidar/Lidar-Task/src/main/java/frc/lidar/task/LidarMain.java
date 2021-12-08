package frc.lidar.task;

import frc.lidar.lib.Lidar;
import frc.lidar.lib.LidarHealth;
import frc.lidar.lib.ScanEntry;
import frc.taskmanager.taskclient.TaskMessenger;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.List;

public class LidarMain {
    public static void main(String[] args) {
        Lidar lidar = new Lidar();
        TaskMessenger msg = new TaskMessenger("localhost", 8264);

        System.out.println("Getting health of lidar");
        lidar.getHealth().thenAccept((health) -> {
            System.out.println("Lidar health is " + health);
            if (health == LidarHealth.ERROR) {
                System.err.println("Lidar is not working!");
                System.exit(-1);
            }

            msg.sendMessage("Ready", new byte[0]);
        });

        List<ScanEntry> scanData = new ArrayList<>();
        lidar.setScanDataCallback((entry) -> {
            synchronized (scanData) {
                scanData.add(entry);
            }
        });
        lidar.setScanStartCallback(() -> {
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(b);
            synchronized (scanData) {
                out.writeInt(scanData.size());
                for (ScanEntry entry : scanData) {
                    out.writeInt(entry.getQuality());
                    out.writeDouble(entry.getAngle());
                    out.writeDouble(entry.getDistance());
                }
                scanData.clear();
            }
            msg.sendMessage("Scan", b.toByteArray());
        });

        boolean scanning = false;
        msg.setMessageCallback((name, data) -> {
            switch (name) {
                case "Start":
                    if (!scanning) {
                        lidar.startScanning();
                    }
                    break;
                case "Stop":
                    if (scanning) {
                        lidar.stopScanning();
                    }
                    break;
                default:
                    System.out.println("Warning: Unknown message received: " + name);
                    break;
            }
        });

        // Run forever
        while (true) {
            msg.read(); // Read incoming messages

            try {
                Thread.sleep(25);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
