package frc.robot.command.auto;

import frc.robot.command.CommandSequence;
import frc.robot.control.SwerveDriveController;
import frc.robot.subsystems.Localization;

public final class AutonomousCommand extends CommandSequence {
    public AutonomousCommand(Localization loc, SwerveDriveController drive) {
        double radius = 1;

        Path path = new Path();
        path.addPoint(0, 5);

        append(new FollowPathCommand(loc, drive, path));
    }
}
