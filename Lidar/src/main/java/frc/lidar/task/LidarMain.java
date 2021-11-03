package frc.lidar.task;

import frc.lidar.lib.Lidar;

public class LidarMain {
    public static void main(String[] args) throws Exception {
        Lidar lidar = new Lidar();
        lidar.startScanning();

        Thread.sleep(10000);
    }
}
