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
import frc.robot.subsystems.Intake;
import frc.robot.subsystems.Localization;
import frc.robot.control.ClimberController;
import frc.robot.subsystems.Shooter;
import frc.robot.subsystems.climber.Climber;
import frc.robot.util.ShuffleBoard;
import frc.robot.util.ShuffleWood;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.TimedRobot;

import static frc.robot.constants.MessengerConstants.*;

public class Robot extends TimedRobot {
  private static final Robot INSTANCE = new Robot();

  public static Robot get() {
    return INSTANCE;
  }

  // Subsystems
  private Input input;

  private AHRS gyro;
  private SwerveDrive drive;
  private SwerveDriveController driveController;

  private MessengerClient msg;
  private MessageDispatcher dispatch;

  private Cameras cameras;
  private CameraTurret cameraTurret;
  private Shooter shooter;
  private Intake intake;
  private ClimberController climber;

  private RobotState state;
  private Localization localization;

  private AutonomousCommand autoCommand;
  
  @Override
  public void robotInit() {
    new ShuffleBoard(); // static init shuffleboard

    state = RobotState.DISABLED;
    Scheduler.get().initState();

    int attempts = 0;
    while (attempts < MESSENGER_MAX_CONNECT_ATTEMPTS && msg == null) {
      try {
        MessengerClient attempt = new MessengerClient(MESSENGER_HOST, MESSENGER_PORT, "RoboRIO", attempts != MESSENGER_MAX_CONNECT_ATTEMPTS - 1);
        msg = attempt;
      } catch (Throwable t) {
        System.err.println("Connect failed, retrying");
        try {
          Thread.sleep(1000);
        } catch (InterruptedException e) {}
      }

      attempts++;
    }
    if (msg == null) {
      throw new IllegalStateException("Messenger is null, this should never happen!");
    }

    dispatch = new MessageDispatcher(msg);

    ShuffleWood.setMessenger(dispatch);

    input = new Input();
    gyro = new AHRS(SPI.Port.kMXP, (byte) 200);

    drive = new SwerveDrive(gyro);
    driveController = new SwerveDriveController(drive, input);

    
    cameras = new Cameras(dispatch);
    cameraTurret = new CameraTurret(cameras);
    localization = new Localization(gyro, drive);
    shooter = new Shooter(driveController, null, input);
    intake = new Intake(input);
    // climber = new ClimberController(input);
    new Climber(input);
    
    driveController.swerveInit();

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
    Scheduler.get().scheduleCommand(autoCommand = new AutonomousCommand(localization, driveController));
  }

  @Override
  public void autonomousPeriodic() {
    driveController.update();
  }

  @Override
  public void teleopInit() {
    state = RobotState.TELEOP;
    Scheduler.get().initState();
    if (autoCommand != null) {
      Scheduler.get().cancelCommand(autoCommand);
      autoCommand = null;
    }
  }

  @Override
  public void teleopPeriodic() {
    driveController.update();
    if (climber != null) climber.groundToSecond();
  }

  @Override
  public void disabledInit() {
    state = RobotState.DISABLED;
    Scheduler.get().initState();
    drive.disable();

    if (autoCommand != null) {
      Scheduler.get().cancelCommand(autoCommand);
      autoCommand = null;
    }
    ShuffleWood.save();
  }

  @Override
  public void disabledPeriodic() {}

  @Override
  public void testInit() {
    state = RobotState.TEST;
    Scheduler.get().initState();
    if (autoCommand != null) {
      Scheduler.get().cancelCommand(autoCommand);
      autoCommand = null;
    }
  }

  @Override
  public void testPeriodic() {}

  public RobotState getState() {
    return state;
  }
}
