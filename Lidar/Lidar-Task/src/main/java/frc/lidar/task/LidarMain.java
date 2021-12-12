package frc.lidar.task;

import frc.lidar.lib.Lidar;
import frc.lidar.lib.LidarHealth;
import frc.lidar.lib.ScanEntry;
import frc.taskmanager.taskclient.TaskMessenger;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LidarMain {
    public static void main(String[] args) {
        Lidar lidar = new Lidar();
        TaskMessenger msg = new TaskMessenger("localhost", 8264, "Lidar");
        
        // Reset the lidar to make sure it's in the expected state
        //lidar.reset();
       // try {
         //   Thread.sleep(10);
        //} catch (InterruptedException e) {
            // Ignore
        //}

        System.out.println("Getting health of lidar");
        lidar.getHealth().thenAccept((health) -> {
            System.out.println("Lidar health is " + health);
            if (health == LidarHealth.ERROR) {
                System.err.println("Lidar is not working!");
                System.exit(-1);
            }

            msg.sendMessage("Ready", new byte[0]);
        });

        lidar.setScanDataCallback((entry) -> {
            try {
                ByteArrayOutputStream b = new ByteArrayOutputStream();
                DataOutputStream out = new DataOutputStream(b);

                    out.writeInt(entry.getQuality());
                    out.writeDouble(entry.getAngle());
                    out.writeDouble(entry.getDistance());

                    msg.sendMessage("Scan", b.toByteArray());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        boolean scanning = false;
        msg.setMessageCallback((name, data) -> {
            System.out.println("Lidar task: Got " + name);
            switch (name) {
                case "Start":
                    System.out.println("Starting scanning");
                    lidar.startScanning();
                    break;
                case "Stop":
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
            msg.read(); // Read incoming messages

            try {
                Thread.sleep(25);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
