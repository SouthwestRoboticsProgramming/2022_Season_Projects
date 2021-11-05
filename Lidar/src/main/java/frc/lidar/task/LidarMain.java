package frc.lidar.task;

import frc.lidar.lib.Lidar;

import java.util.Scanner;

public class LidarMain {
    public static void main(String[] args) {
        Lidar lidar = new Lidar();
        lidar.getHealth().thenAccept((health) -> System.out.println("Health: " + health));

        lidar.setScanStartCallback(() -> System.out.println("Scan started!"));
        lidar.setScanDataCallback((entry) -> {
            if (entry.getDistance() == 0) return;
            if (entry.getQuality() == 0) return;

            System.out.println("Scan data: " + entry);
        });

        Scanner scanner = new Scanner(System.in);
        boolean running = true;
        while (running) {
            String line = scanner.nextLine();

            // Basic command-line interface
            switch (line) {
                case "exit":
                    running = false;
                    break;
                case "scan":
                    lidar.startScanning();
                    break;
                case "stop":
                    lidar.stopScanning();
                    break;
                case "health":
                    lidar.getHealth().thenAccept((health) -> System.out.println("Health: " + health));
                    break;
                case "info":
                    lidar.getInfo().thenAccept((info) -> System.out.println("Info: " + info));
                    break;
            }
        }

        lidar.close();
    }
}
