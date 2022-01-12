package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import frc.robot.control.Input;
import frc.robot.control.SwerveDriveController;
import frc.robot.control.XboxController;
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

    // XboxController x = input.controller;
    // if (x.getXButton()) {
    //   drive.w1.spin(0.3);
    // } else {
    //   drive.w1.spin(0);
    // }
    // if (x.getYButton()) {
    //   drive.w2.spin(0.3);
    // } else {
    //   drive.w2.spin(0);
    // }
    // if (x.getBButton()) {
    //   drive.w3.spin(0.3);
    // } else {
    //   drive.w3.spin(0);
    // }
    // if (x.getAButton()) {
    //   drive.w4.spin(0.3);
    // } else {
    //   drive.w4.spin(0);
    // }

    System.out.printf("Encoders: %3.3f %3.3f %3.3f %3.3f %n", drive.w1.getEncoder(), drive.w2.getEncoder(), drive.w3.getEncoder(), drive.w4.getEncoder());
    System.out.printf("Targets:  %3.3f %3.3f %3.3f %3.3f %n", drive.w1.getTarget(), drive.w2.getTarget(), drive.w3.getTarget(), drive.w4.getTarget());
  }

  @Override
  public void disabledInit() {
    drive.disable();
  }

  @Override
  public void disabledPeriodic() {}

  @Override
  public void testInit() {}

  @Override
  public void testPeriodic() {}
}
