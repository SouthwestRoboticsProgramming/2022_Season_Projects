package frc.lidar.task;

import frc.lidar.lib.Lidar;

import java.util.Scanner;

public class LidarMain {
    public static void main(String[] args) {
        Lidar lidar = new Lidar();
        lidar.getHealth().thenAccept((health) -> {
            System.out.println("H " + health);
        });

        lidar.setScanStartCallback(() -> System.out.println("S " + System.currentTimeMillis()));
        lidar.setScanDataCallback((entry) -> {
            if (entry.getDistance() == 0) return;
            if (entry.getQuality() == 0) return;

            System.out.println("s " + entry.getQuality() + " " + entry.getDistance() + " " + entry.getAngle());
        });

        try {
            Thread.sleep(1000);
        } catch (Exception e) {}

        System.out.println("M The lidar is starting");
        lidar.startScanning();

        // Run forever
        while (true) {
            try {
                Thread.sleep(1000000);
            } catch (Exception e) {}
        }
    }
}
