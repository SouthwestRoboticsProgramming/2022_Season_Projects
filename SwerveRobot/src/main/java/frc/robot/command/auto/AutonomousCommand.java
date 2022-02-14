package frc.robot.command.auto;

import frc.robot.command.CommandSequence;
import frc.robot.control.SwerveDriveController;
import frc.robot.subsystems.Localization;

public final class AutonomousCommand extends CommandSequence {
    public AutonomousCommand(Localization loc, SwerveDriveController drive) {
        Path path = new Path();
        path.addPoint(1, 0);
        path.addPoint(1, 1);
        path.addPoint(0, 1);
        path.addPoint(0, 0);

        append(new FollowPathCommand(loc, drive, path));
    }
}
