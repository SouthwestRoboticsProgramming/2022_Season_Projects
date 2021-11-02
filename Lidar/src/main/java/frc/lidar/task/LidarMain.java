package frc.lidar.task;

import com.fazecast.jSerialComm.SerialPort;

public class LidarMain {
    public static void main(String[] args) {
        SerialPort lidarPort = null;
        System.out.println("Available serial ports:");
        for (SerialPort port : SerialPort.getCommPorts()) {
            System.out.println(port.getSystemPortName() + " [" + port.getDescriptivePortName() + "]: " + port.getPortDescription());

            if (port.getPortDescription().contains("CP2102")) {
                if (lidarPort != null) {
                    throw new RuntimeException("Port conflict, are multiple lidars connected?");
                }
                lidarPort = port;
            }
        }
        if (lidarPort == null) {
            throw new RuntimeException("Could not find lidar");
        }
        System.out.println("Found lidar on port " + lidarPort.getSystemPortName());
    }
}
