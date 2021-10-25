package frc.visualizer.controller;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import frc.visualizer.Point;
import frc.visualizer.Robot;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.List;

public class PhysicalRobotController implements RobotController {
    private final NetworkTableEntry predictedX, predictedY;
    private final NetworkTableEntry predictedAngle;
    private final NetworkTableEntry leftRotation, rightRotation;
    private final NetworkTableEntry path;

    public PhysicalRobotController(String tableId, int teamNumber) {
        NetworkTableInstance inst = NetworkTableInstance.getDefault();
        NetworkTable table = inst.getTable(tableId);
        predictedX = table.getEntry("predictedX");
        predictedY = table.getEntry("predictedY");
        predictedAngle = table.getEntry("predictedAngle");
        leftRotation = table.getEntry("leftRotation");
        rightRotation = table.getEntry("rightRotation");
        path = table.getEntry("path");

        inst.startClientTeam(teamNumber);
        inst.startDSClient();
    }

    @Override
    public void update(Robot robot) {
        robot.setPredictedX(predictedX.getDouble(0.0));
        robot.setPredictedY(predictedY.getDouble(0.0));
        robot.setPredictedAngle(predictedAngle.getDouble(0.0));
        robot.setLeftRotation(leftRotation.getDouble(0.0));
        robot.setRightRotation(rightRotation.getDouble(0.0));

        try {
            byte[] pathData = path.getRaw(new byte[] {0, 0, 0, 0});
            DataInputStream in = new DataInputStream(new ByteArrayInputStream(pathData));

            List<Point> points = robot.getPath();
            points.clear();

            int nodeCount = in.readInt();
            for (int i = 0; i < nodeCount; i++) {
                double x = in.readDouble();
                double y = in.readDouble();
                points.add(new Point(x, y));
            }
        } catch (IOException e) {
            System.err.println("Failed to decode robot path");
            e.printStackTrace();
        }
    }
}
