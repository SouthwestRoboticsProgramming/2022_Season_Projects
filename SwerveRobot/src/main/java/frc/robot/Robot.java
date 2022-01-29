package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj2.command.CommandScheduler;

import com.kauailabs.navx.frc.AHRS;
import frc.messenger.client.MessengerClient;
import frc.robot.control.Input;
import frc.robot.control.SwerveDriveController;
import frc.robot.drive.SwerveDrive;
import frc.robot.subsystems.Localization;
import frc.robot.util.ShuffleWood;
import edu.wpi.first.wpilibj.SPI;

public class Robot extends TimedRobot {
  private Input input;
  private SwerveDrive drive;
  private SwerveDriveController driveController;
  private Localization localization;
  private MessengerClient msg;

  @Override
  public void robotInit() {
    AHRS gyro = new AHRS(SPI.Port.kMXP, (byte) 200);

    input = new Input();
    drive = new SwerveDrive(gyro);
    driveController = new SwerveDriveController(drive, input);

    driveController.swerveInit();

    msg = new MessengerClient("10.21.29.3", 8341, "RoboRIO");
    ShuffleWood.setMessenger(msg);

    localization = new Localization(gyro);
  }

  @Override
  public void robotPeriodic() {
    msg.read();
    CommandScheduler.getInstance().run();
  }

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
