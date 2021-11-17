package frc.taskmanager.client;

public class Something {
    public static void main(String[] args) {
        Coprocessor cp = new Coprocessor("robopi.local", 8263);
        cp.connect();

        Task lidar = cp.getTask("Lidar");
        lidar.start();
        lidar.setMessageReceiveCallback((type, data) -> {

        });
    }
}
