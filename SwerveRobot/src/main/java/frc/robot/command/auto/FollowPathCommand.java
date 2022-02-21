package frc.robot.command.auto;

import java.util.List;

import frc.robot.command.Command;
import frc.robot.command.auto.Path.Point;
import frc.robot.control.SwerveDriveController;
import frc.robot.subsystems.Localization;

import static frc.robot.constants.AutonomousConstants.*;

public class FollowPathCommand implements Command {
    private final Localization loc;
    private final SwerveDriveController drive;
    private final List<Point> path;
    private int targetIndex;
    private Point currentTarget;

    public FollowPathCommand(Localization loc, SwerveDriveController drive, Path path) {
        this.loc = loc;
        this.drive = drive;
        this.path = path.getPath();
        reset();
    }

    public void reset() {
        targetIndex = 0;
        currentTarget = path.get(0);
    }

    private boolean isAtTarget() {
        double deltaX = currentTarget.x - loc.getX();
        double deltaY = currentTarget.y - loc.getY();
        double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);

        return distance < AUTO_TARGET_THRESHOLD;
    }

    @Override
    public boolean run() {
        System.out.printf("Localization: %3.3f %3.3f %n", loc.getX(), loc.getY());

        if (isAtTarget()) {
            targetIndex++;
            if (targetIndex >= path.size()) {
                return true;
            }
            currentTarget = path.get(targetIndex);
        }

        double locX = loc.getX();
        double locY = loc.getY();

        double deltaX = currentTarget.x - locX;
        double deltaY = currentTarget.y - locY;
        double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        deltaX /= distance;
        deltaY /= distance;

        deltaX *= AUTO_DRIVE_SCALE;
        deltaY *= AUTO_DRIVE_SCALE;

        System.out.println(deltaX + " " + deltaY);
        drive.drive(deltaX, deltaY, 0);

        return false;
    }
}
