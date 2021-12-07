package frc.robot;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

import frc.robot.path.Point;

public class VisualizerCommunicator {
    private static final int PORT = 8372;

    private DataOutputStream out = null;

    public VisualizerCommunicator() {
        new Thread(() -> {
            try {
                ServerSocket ss = new ServerSocket(PORT);
                System.out.println("Awaiting visualizer connection");
                Socket connection = ss.accept();
                System.out.println("Visualizer has connected");

                DataOutputStream out = new DataOutputStream(connection.getOutputStream());
                this.setStream(out);
            } catch (Exception e) {
                throw new RuntimeException("Visualizer connection failed!");
            }
        }).start();
    }

    private synchronized void setStream(DataOutputStream out) {
        this.out = out;
    }

    public boolean connected() {
        return out != null;
    }

    public void setPredictedX(double x) {
        try {
            out.writeByte(0);
            out.writeDouble(x);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    public void setPredictedY(double y) {
        try {
            out.writeByte(1);
            out.writeDouble(y);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    public void setPredictedAngle(double angle) {
        try {
            out.writeByte(2);
            out.writeDouble(angle);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    // Sends the current memory usage of the robot
    public void setMemUsage() {
        Runtime rt = Runtime.getRuntime();
        long total = rt.maxMemory();
        long free = rt.freeMemory();
        long usage = total - free;

        try {
            out.writeByte(5);
            out.writeLong(total);
            out.writeLong(usage);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    public void setTPS(double tps) {
        try {
            out.writeByte(6);
            out.writeDouble(tps);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    public void setWheelRotations(double left, double right) {
        try {
            out.writeByte(7);
            out.writeDouble(left);
            out.writeDouble(right);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    public void setPath(List<Point> path) {
        try {
            out.writeByte(4);
            out.writeInt(path.size());
            for (Point point : path) {
                out.writeDouble(point.getX());
                out.writeDouble(point.getY());
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }
}
