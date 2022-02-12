package frc.lidar.task;

import frc.lidar.lib.Lidar;
import frc.lidar.lib.LidarHealth;
import frc.messenger.client.MessengerClient;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class LidarMain {
    public static void main(String[] args) {
        Lidar lidar = new Lidar();
        MessengerClient msg = new MessengerClient("localhost", 5805, "Lidar");

        msg.listen("Lidar:Start");
        msg.listen("Lidar:Stop");

        System.out.println("Getting health of lidar");
        lidar.getHealth().thenAccept((health) -> {
            System.out.println("Lidar health is " + health);
            if (health == LidarHealth.ERROR) {
                System.err.println("Lidar is not working!");
                System.exit(-1);
            }

            msg.sendMessage("Lidar:Ready", new byte[0]);
        });

        lidar.setScanDataCallback((entry) -> {
            try {
                ByteArrayOutputStream b = new ByteArrayOutputStream();
                DataOutputStream out = new DataOutputStream(b);

                out.writeInt(entry.getQuality());
                out.writeDouble(entry.getAngle());
                out.writeDouble(entry.getDistance());

                synchronized (msg) {
                    msg.sendMessage("Lidar:Scan", b.toByteArray());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        lidar.setScanStartCallback(() -> {
            synchronized (msg) {
                msg.sendMessage("Lidar:ScanStart", new byte[0]);
            }
        });

        msg.setCallback((name, data) -> {
            System.out.println("Lidar task: Got " + name);
            switch (name) {
                case "Lidar:Start":
                    System.out.println("Starting scanning");
                    lidar.startScanning();
                    break;
                case "Lidar:Stop":
                    System.out.println("Stopping scanning");
                    lidar.stopScanning();
                    break;
                default:
                    System.out.println("Warning: Unknown message received: " + name);
                    break;
            }
        });

        // Run forever
        while (true) {
            synchronized (msg) {
                msg.read(); // Read incoming messages
            }

            try {
                Thread.sleep(25);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
