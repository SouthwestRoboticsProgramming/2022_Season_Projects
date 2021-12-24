package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import frc.robot.control.Input;
import frc.robot.control.SwerveDriveController;
import frc.robot.drive.SwerveDrive;

public class Robot extends TimedRobot {
  private Input input;
  private SwerveDrive drive;
  private SwerveDriveController driveController;

  @Override
  public void robotInit() {
    input = new Input();

    drive = new SwerveDrive();
    driveController = new SwerveDriveController(drive, input);
  }

  @Override
  public void robotPeriodic() {}

  @Override
  public void autonomousInit() {}

  @Override
  public void autonomousPeriodic() {}

  @Override
  public void teleopInit() {}

  @Override
  public void teleopPeriodic() {
    driveController.update();
  }

  @Override
  public void disabledInit() {}

  @Override
  public void disabledPeriodic() {}

  @Override
  public void testInit() {}

  @Override
  public void testPeriodic() {}
}
