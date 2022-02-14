package frc.robot.command.auto;

import frc.robot.command.CommandSequence;
import frc.robot.control.SwerveDriveController;
import frc.robot.subsystems.Localization;

public final class AutonomousCommand extends CommandSequence {
    public AutonomousCommand(Localization loc, SwerveDriveController drive) {
        double radius = 1;

        Path path = new Path();
        for (double angle = 0; angle < Math.PI * 2; angle += Math.PI / 256) {
            path.addPoint(Math.cos(angle) * radius - radius, Math.sin(angle) * radius);
        }

        append(new FollowPathCommand(loc, drive, path));
    }
}
