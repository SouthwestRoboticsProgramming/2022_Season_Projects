package frc.robot.path;

import java.util.List;

import frc.robot.DriveTrain;
import frc.robot.Localizer;
import frc.robot.Utils;

public final class PathFollower {
    private final Localizer localizer;
    private final DriveTrain drive;
    private final double distanceToleranceSq;
    private final double angleTolerance;
    private final double speed;
    private List<Point> path;
    private Point target;
    private int targetIndex;

    public PathFollower(Localizer localizer, DriveTrain drive, double speed, double positionTolerance, double angleTolerance) {
        this.localizer = localizer;
        this.drive = drive;
        this.speed = speed;
        this.distanceToleranceSq = positionTolerance * positionTolerance;
        this.angleTolerance = Math.toRadians(angleTolerance);
        this.path = null;
        this.target = null;
        this.targetIndex = -1;
    }

    public void setPath(List<Point> path) {
        this.path = path;
        if (path.size() > 0) {
            target = path.get(0);
        }
        targetIndex = 0;
    }

    public void update() {
        if (path == null || target == null) {
            return;
        }

        double posX = localizer.getX();
        double posY = localizer.getY();
        double posRot = Utils.normalizeAngle(localizer.getRotationRadians());

        double left = 0;
        double right = 0;

        double deltaX = target.getX() - posX;
        double deltaY = target.getY() - posY;
        double distToTargetSq = deltaX * deltaX + deltaY * deltaY;
        if (distToTargetSq < distanceToleranceSq) {
            targetIndex++;
            if (targetIndex < path.size()) {
                target = path.get(targetIndex);
            } else {
                target = null;
            }
        } else {
            double angle = -Math.atan2(-deltaY, deltaX);
            double angleDiff = Utils.normalizeAngle(angle - posRot);
            if (Math.abs(angleDiff) > angleTolerance) {
                if (angleDiff > 0) {
                    left = -1;
                    right = 1;
                } else {
                    left = 1;
                    right = -1;
                }
            } else {
                left = 1;
                right = 1;
            }
        }

        left *= speed;
        right *= speed;

        drive.driveMotors(left, right);
    }
}
