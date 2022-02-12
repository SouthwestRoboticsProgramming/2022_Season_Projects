package frc.robot.command.auto;

import java.util.List;

import frc.robot.command.Command;
import frc.robot.command.auto.Path.Point;
import frc.robot.subsystems.Localization;

public class FollowPathCommand implements Command {
    private final Localization loc;
    private final List<Point> path;
    private int targetIndex;
    private Point currentTarget;

    public FollowPathCommand(Localization loc, Path path) {
        this.loc = loc;
        this.path = path.getPath();
        reset();
    }

    public void reset() {
        targetIndex = 0;
        currentTarget = path.get(0);
    }

    private boolean isAtTarget() {
        return false;
    }

    @Override
    public boolean run() {
        if (isAtTarget()) {
            targetIndex++;
            if (targetIndex >= path.size()) {
                return true;
            }
            currentTarget = path.get(targetIndex);
        }

        double locX = loc.getX();
        double locY = loc.getY();

        // get angle to target

        // point wheels at angle

        // drive

        return false;
    }
}
