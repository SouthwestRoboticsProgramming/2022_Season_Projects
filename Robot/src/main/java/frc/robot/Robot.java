package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;

public final class Robot extends TimedRobot {
  private DriveTrain driveTrain;
  private DriveController driveController;

  @Override
  public void robotInit() {
    XboxController controller = new XboxController(0);
    Input input = new Input(controller);
    
    driveTrain = new DriveTrain();
    driveController = new DriveController(driveTrain, input);
  }

  @Override
  public void robotPeriodic() {}

  @Override
  public void disabledInit() {
    driveTrain.stopMotors();
  }

  @Override
  public void disabledPeriodic() {}

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
  public void testInit() {}

  @Override
  public void testPeriodic() {}
}