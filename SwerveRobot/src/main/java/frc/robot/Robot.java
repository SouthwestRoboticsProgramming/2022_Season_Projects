package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj2.command.CommandScheduler;

import com.kauailabs.navx.frc.AHRS;

import frc.messenger.client.MessageDispatcher;
import frc.messenger.client.MessengerClient;
import frc.robot.control.Input;
import frc.robot.control.SwerveDriveController;
import frc.robot.drive.SwerveDrive;
import frc.robot.subsystems.CameraTurret;
import frc.robot.subsystems.Cameras;
import frc.robot.subsystems.Localization;
import frc.robot.util.ShuffleWood;
import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.wpilibj.SPI;

import static frc.robot.Constants.*;

public class Robot extends TimedRobot {
  private Input input;
  //private SwerveDrive drive;
  //private SwerveDriveController driveController;
  private MessengerClient msg;
  private MessageDispatcher dispatch;

  // Subsystems
  private Localization localization;
  private CameraTurret cameraTurret;
  private Cameras cameras;

  @Override
  public void robotInit() {
    AHRS gyro = new AHRS(SPI.Port.kMXP, (byte) 200);

    input = new Input();
    //drive = new SwerveDrive(gyro);
    //driveController = new SwerveDriveController(drive, input);

    //driveController.swerveInit();

    while (msg == null) {
      try {
        MessengerClient attempt = new MessengerClient(MESSENGER_HOST, MESSENGER_PORT, "RoboRIO");
        msg = attempt;
      } catch (Throwable t) {
        System.err.print("Connect failed, retrying");
        try {
          Thread.sleep(1000);
        } catch (InterruptedException e) {}
      }
    }
    ShuffleWood.setMessenger(msg);
    dispatch = new MessageDispatcher(msg);

    localization = new Localization(gyro);
    cameraTurret = new CameraTurret();
    cameras = new Cameras(dispatch);
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
    //driveController.update();
  }

  @Override
  public void disabledInit() {
    //drive.disable();
  }

  @Override
  public void disabledPeriodic() {}

  @Override
  public void testInit() {}

  @Override
  public void testPeriodic() {}
}
