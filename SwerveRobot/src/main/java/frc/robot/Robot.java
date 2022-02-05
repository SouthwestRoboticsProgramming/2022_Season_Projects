package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj2.command.CommandScheduler;

import com.kauailabs.navx.frc.AHRS;

import frc.messenger.client.MessageDispatcher;
import frc.messenger.client.MessengerClient;
import frc.robot.control.Input;
import frc.robot.control.SwerveDriveController;
import frc.robot.drive.SwerveDrive;
import frc.robot.util.ShuffleWood;
import edu.wpi.first.wpilibj.SPI;

import static frc.robot.Constants.*;

public class Robot extends TimedRobot {
  private static final int SHUFFLEWOOD_SAVE_INTERVAL = 50;

  private AHRS gyro;
  private Input input;
  private SwerveDrive drive;
  private SwerveDriveController driveController;
  private MessengerClient msg;
  private MessageDispatcher dispatch;
  private int shufflewoodSaveTimer = SHUFFLEWOOD_SAVE_INTERVAL;

  // Subsystems
  // private Localization localization;
  // private Cameras cameras;
  // private CameraTurret cameraTurret;

  @Override
  public void robotInit() {
    gyro = new AHRS(SPI.Port.kMXP, (byte) 200);

    input = new Input();
    drive = new SwerveDrive(gyro);
    driveController = new SwerveDriveController(drive, input);

    driveController.swerveInit();

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
    dispatch = new MessageDispatcher(msg);

    ShuffleWood.setMessenger(dispatch);

    /*cameras = new Cameras(dispatch);
    cameraTurret = new CameraTurret();
    localization = new Localization(gyro, cameraTurret);*/

    //ShuffleWood.setInt("TEST", 736219837);
  }

  @Override
  public void robotPeriodic() {
    msg.read();
    CommandScheduler.getInstance().run();

    if (shufflewoodSaveTimer-- == 0) {
      shufflewoodSaveTimer = SHUFFLEWOOD_SAVE_INTERVAL;

      ShuffleWood.save();
    }
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
    ShuffleWood.save();
  }

  @Override
  public void disabledPeriodic() {}

  @Override
  public void testInit() {}

  @Override
  public void testPeriodic() {}
}
