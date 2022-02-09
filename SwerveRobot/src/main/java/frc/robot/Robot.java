package frc.robot;

import com.kauailabs.navx.frc.AHRS;

import frc.messenger.client.MessageDispatcher;
import frc.messenger.client.MessengerClient;
import frc.robot.command.SaveShuffleWoodCommand;
import frc.robot.command.auto.AutonomousCommand;
import frc.robot.control.Input;
import frc.robot.control.SwerveDriveController;
import frc.robot.drive.SwerveDrive;
import frc.robot.subsystems.Shooter;
import frc.robot.util.ShuffleWood;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.TimedRobot;

import static frc.robot.Constants.*;

public class Robot extends TimedRobot {
  private static final Robot INSTANCE = new Robot();

  public static Robot get() {
    return INSTANCE;
  }

  private RobotState state;
  private AHRS gyro;
  private Input input;
  private SwerveDrive drive;
  private SwerveDriveController driveController;
  private MessengerClient msg;
  private MessageDispatcher dispatch;

  // Subsystems
  // private Localization localization;
  // private Cameras cameras;
  // private CameraTurret cameraTurret;
  private Shooter shooter;

  @Override
  public void robotInit() {
    state = RobotState.DISABLED;
    Scheduler.get().initState();

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

    gyro = new AHRS(SPI.Port.kMXP, (byte) 200);

    input = new Input();
    drive = new SwerveDrive(gyro);
    driveController = new SwerveDriveController(drive, input);

    driveController.swerveInit();

    /*cameras = new Cameras(dispatch);
    cameraTurret = new CameraTurret();
    localization = new Localization(gyro, cameraTurret);*/
    shooter = new Shooter(driveController, input);

    //ShuffleWood.setInt("TEST", 736219837);

    Scheduler.get().scheduleCommand(new SaveShuffleWoodCommand());
  }

  @Override
  public void robotPeriodic() {
    msg.read();
    Scheduler.get().update();
  }

  @Override
  public void autonomousInit() {
    state = RobotState.AUTONOMOUS;
    Scheduler.get().initState();
    Scheduler.get().scheduleCommand(new AutonomousCommand());
  }

  @Override
  public void autonomousPeriodic() {}

  @Override
  public void teleopInit() {
    state = RobotState.TELEOP;
    Scheduler.get().initState();
  }

  @Override
  public void teleopPeriodic() {
    driveController.update();
  }

  @Override
  public void disabledInit() {
    state = RobotState.DISABLED;
    Scheduler.get().initState();
    drive.disable();
    ShuffleWood.save();
  }

  @Override
  public void disabledPeriodic() {}

  @Override
  public void testInit() {
    state = RobotState.TEST;
    Scheduler.get().initState();
  }

  @Override
  public void testPeriodic() {}

  public RobotState getState() {
    return state;
  }
}
