package frc.robot.path;

import java.util.List;

import frc.robot.DriveTrain;
import frc.robot.Localizer;
import frc.robot.util.Utils;

public final class PathFollower {
    private final Localizer localizer;
    private final DriveTrain drive;
    private final double distanceToleranceSq;
    private final double angleTolerance;
    private final double speed;
    private final double angleSlowdownPoint;
    private List<Point> path;
    private Point target;
    private int targetIndex;

    public PathFollower(Localizer localizer, DriveTrain drive, double speed, double positionTolerance, double angleTolerance, double angleSlowdownPoint) {
        this.localizer = localizer;
        this.drive = drive;
        this.speed = speed;
        this.angleSlowdownPoint = Math.toRadians(angleSlowdownPoint);
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

    public boolean isDone() {
        return target == null;
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
            System.out.println(angleDiff);
            if (Math.abs(angleDiff) > angleTolerance) {
                //System.out.println((Math.abs(angleDiff) - angleTolerance) / angleSlowdownPoint);
                double speedCutoff = Utils.clamp((Math.abs(angleDiff) - angleTolerance) / angleSlowdownPoint, 0.5, 1);
                //System.out.println("Speed cutoff: " + speedCutoff);

                if (angleDiff > 0) {
                    left = -1;
                    right = 1;
                } else {
                    left = 1;
                    right = -1;
                }

                left *= speedCutoff;
                right *= speedCutoff;
            } else {
                left = 1;
                right = 1;
            }
        }

        left *= speed;
        right *= speed;

        drive.driveMotors(left, right);
        System.out.println("Driving (" + left + ", " + right + ")");

        //System.out.println(localizer.getRotationRadians());
    }
}
