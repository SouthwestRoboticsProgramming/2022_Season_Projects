package frc.visualizer.controller;

import frc.visualizer.Point;
import frc.visualizer.Robot;
import frc.visualizer.TreeNode;

import java.io.DataInputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VirtualRobotController implements RobotController {
    private final DataInputStream in;

    private double predictedX, predictedY;
    private double predictedAngle;
    private TreeNode pathTree;

    public VirtualRobotController() {
        predictedX = 0;
        predictedY = 0;
        predictedAngle = 0;
        pathTree = new TreeNode(0, 0, Collections.emptyList());

        try {
            System.out.println("Connecting to virtual robot...");
            Socket socket = new Socket("10.21.29.2", 8372);
            if (!socket.isConnected()) {
                throw new RuntimeException("Failed to connect!");
            } else {
                System.out.println("Connected");
            }
            in = new DataInputStream(socket.getInputStream());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void readPredictedX() throws Exception {
        predictedX = in.readDouble();
    }

    private void readPredictedY() throws Exception {
        predictedY = in.readDouble();
    }

    private void readPredictedAngle() throws Exception {
        predictedAngle = in.readDouble();
    }

    private void readPathTree() throws Exception {
        pathTree = readTreeNode();
    }

    private TreeNode readTreeNode() throws Exception {
        double x = in.readDouble();
        double y = in.readDouble();
        int childCount = in.readInt();
        List<TreeNode> children = new ArrayList<>();
        for (int i = 0; i < childCount; i++) {
            children.add(readTreeNode());
        }
        return new TreeNode(x, y, children);
    }

    private void readPath(List<Point> path) throws Exception {
        path.clear();

        int count = in.readInt();
        for (int i = 0; i < count; i++) {
            double x = in.readDouble();
            double y = in.readDouble();
            path.add(new Point(x, y));
        }
    }

    private void readMemUsage(Robot robot) throws Exception {
        long total = in.readLong();
        long usage = in.readLong();
        robot.setMemTotal(total);
        robot.setMemUsed(usage);
    }

    private void readTPS(Robot robot) throws Exception {
        double tps = in.readDouble();
        robot.setTicksPerSecond(tps);
    }

    private void readWheelRotations(Robot robot) throws Exception {
        double left = in.readDouble();
        double right = in.readDouble();
        robot.setLeftRotation(left * Math.PI * 2);
        robot.setRightRotation(right * Math.PI * 2);
    }

    @Override
    public void update(Robot robot) {
        try {
            while (in.available() > 0) {
                byte id = in.readByte();
                switch (id) {
                    case 0: readPredictedX(); break;
                    case 1: readPredictedY(); break;
                    case 2: readPredictedAngle(); break;
                    case 3: readPathTree(); break;
                    case 4: readPath(robot.getPath()); break;
                    case 5: readMemUsage(robot); break;
                    case 6: readTPS(robot); break;
                    case 7: readWheelRotations(robot); break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        robot.setPredictedX(predictedX);
        robot.setPredictedY(predictedY);
        robot.setPredictedAngle(predictedAngle);
        robot.setPathTree(pathTree);
    }
}
