package frc.virtualrobot.robot;

import frc.virtualrobot.robot.path.Point;
import frc.virtualrobot.robot.path.Tree;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Set;

public class VisualizerCommunicator {
    private static final int PORT = 8372;

    private final DataOutputStream out;

    public VisualizerCommunicator() {
        try {
            ServerSocket ss = new ServerSocket(PORT);
            System.out.println("Waiting for visualizer to connect...");
            Socket connection = ss.accept();
            System.out.println("Visualizer has connected");

            out = new DataOutputStream(connection.getOutputStream());
        } catch (Exception e) {
            throw new RuntimeException("Visualizer connection failed!");
        }
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

    public void setPathTree(Tree tree) {
        Point root = tree.getRoot();
        try {
            out.writeByte(3);
            writeTreeNode(root, tree);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    public void setPath(List<Point> path) {
        try {
            out.writeByte(4);
            out.writeInt(path.size());
            for (Point p : path) {
                out.writeDouble(p.getX());
                out.writeDouble(p.getY());
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    // Sends the current memory usage of the robot
    public void setMemUsage() {
        Runtime rt = Runtime.getRuntime();
        long total = rt.totalMemory();
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

    private void writeTreeNode(Point p, Tree tree) throws IOException {
        out.writeDouble(p.getX());
        out.writeDouble(p.getY());
        Set<Point> children = tree.getChildren(p);
        out.writeInt(children.size());
        for (Point child : children) {
            writeTreeNode(child, tree);
        }
    }
}
