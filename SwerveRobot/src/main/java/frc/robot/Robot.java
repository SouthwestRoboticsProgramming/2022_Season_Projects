package frc.robot;

import com.kauailabs.navx.frc.AHRS;

import frc.messenger.client.MessageDispatcher;
import frc.messenger.client.MessengerClient;
import frc.robot.command.SaveShuffleWoodCommand;
import frc.robot.command.auto.AutonomousCommand;
import frc.robot.control.Input;
import frc.robot.control.SwerveDriveController;
import frc.robot.drive.SwerveDrive;
import frc.robot.subsystems.CameraTurret;
import frc.robot.subsystems.Cameras;
import frc.robot.subsystems.Localization;
import frc.robot.subsystems.Shooter;
import frc.robot.util.ShuffleWood;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.TimedRobot;

import static frc.robot.constants.MessengerConstants.*;

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
  private Localization localization;
  private Cameras cameras;
  private CameraTurret cameraTurret;
  private Shooter shooter;

  @Override
  public void robotInit() {
    System.out.println("Hello from robot");

    state = RobotState.DISABLED;
    Scheduler.get().initState();

    System.out.println("Inited");

    // while (msg == null) {
    //   try {
    //     MessengerClient attempt = new MessengerClient(MESSENGER_HOST, MESSENGER_PORT, "RoboRIO");
    //     msg = attempt;
    //   } catch (Throwable t) {
    //     System.err.print("Connect failed, retrying");
    //     try {
    //       Thread.sleep(1000);
    //     } catch (InterruptedException e) {}
    //   }
    // }
    // dispatch = new MessageDispatcher(msg);

    System.out.println("Connected");

    // ShuffleWood.setMessenger(dispatch);

    System.out.println("Shufflewood on");

    gyro = new AHRS(SPI.Port.kMXP, (byte) 200);

    input = new Input();
    drive = new SwerveDrive(gyro);
    driveController = new SwerveDriveController(drive, input);

    driveController.swerveInit();

    System.out.println("drive on");

    // cameras = new Cameras(dispatch);
    // cameraTurret = new CameraTurret(cameras);
    // localization = new Localization(gyro, cameraTurret);
    // shooter = new Shooter(driveController, cameraTurret, input);

    //ShuffleWood.setInt("TEST", 736219837);

    // Scheduler.get().scheduleCommand(new SaveShuffleWoodCommand());
    System.out.println("Scheduled");
  }

  @Override
  public void robotPeriodic() {
    //System.out.println("periodic");

    //msg.read();
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
    // ShuffleWood.save();
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
