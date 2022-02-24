package frc.robot.command.drive;

import edu.wpi.first.wpilibj2.command.SwerveControllerCommand;
import frc.robot.command.Command;
import frc.robot.control.SwerveDriveController;

public class TurnToAngle implements Command {

  private final SwerveDriveController drive;
  private final double angle;
  public TurnToAngle(SwerveDriveController drive, double angle) {
    this.drive = drive;
    this.angle = angle;
  }

  @Override
  public boolean run() {
    drive.turnToTarget(angle);

    return drive.isAtTarget();
  }
}
