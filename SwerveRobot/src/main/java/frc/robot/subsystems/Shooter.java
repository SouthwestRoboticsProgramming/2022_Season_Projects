package frc.robot.subsystems;

import frc.robot.Scheduler;
import frc.robot.command.shooter.IndexBall;
import frc.robot.control.Input;
import frc.robot.control.SwerveDriveController;
import frc.robot.util.ShuffleBoard;
import frc.robot.util.Utils;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.motorcontrol.can.TalonFXConfiguration;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.TalonSRXConfiguration;

import edu.wpi.first.wpilibj.DigitalInput;

import static frc.robot.constants.ShooterConstants.*;


public class Shooter extends Subsystem {
  private final Input input;
  private final SwerveDriveController driveController;
  private final CameraTurret cameraTurret;
  private final TalonFX flywheel;
  private final TalonFX index;
  private final TalonSRX hood;
  private final DigitalInput hoodLimit;

  private double distance = 0;
  private double angle = 0;

  private boolean calibratingHood = true;

  public Shooter(SwerveDriveController swerveDriveController, CameraTurret camera, Input input) {
    this.input = input;
    driveController = swerveDriveController;
    
    cameraTurret = camera;
    flywheel = new TalonFX(FLYWHEEL_MOTOR_ID);
    index = new TalonFX(INDEX_MOTOR_ID);
    hood = new TalonSRX(HOOD_MOTOR_ID);

    index.setInverted(true);
    flywheel.setInverted(true);
    hood.setInverted(true);

    hoodLimit = new DigitalInput(HOOD_LIMIT_CHANNEL);

    TalonFXConfiguration flywheelConfig = new TalonFXConfiguration();
    flywheelConfig.neutralDeadband = 0.001;
    flywheelConfig.slot0.kF = FLYWHEEL_KF;
    flywheelConfig.slot0.kP = FLYWHEEL_KP;
    flywheelConfig.slot0.kI = FLYWHEEL_KI;
    flywheelConfig.slot0.kD = FLYWHEEL_KD;
    flywheelConfig.slot0.closedLoopPeakOutput = 1;
    flywheelConfig.openloopRamp = 0.5;
    flywheelConfig.closedloopRamp = 0.5;
    flywheel.configAllSettings(flywheelConfig);
    flywheel.configSelectedFeedbackSensor(FeedbackDevice.IntegratedSensor);

    TalonFXConfiguration indexConfig = new TalonFXConfiguration();
    indexConfig.neutralDeadband = 0.001;
    indexConfig.slot0.kF = INDEX_KF;
    indexConfig.slot0.kP = INDEX_KP;
    indexConfig.slot0.kI = INDEX_KI;
    indexConfig.slot0.kD = INDEX_KD;
    indexConfig.slot0.closedLoopPeakOutput = 1;
    indexConfig.openloopRamp = 0.5;
    indexConfig.closedloopRamp = 0.5;
    index.configAllSettings(indexConfig);
    index.configSelectedFeedbackSensor(FeedbackDevice.IntegratedSensor);

    TalonSRXConfiguration hoodConfig = new TalonSRXConfiguration();
    hoodConfig.neutralDeadband = 0.001;    
    indexConfig.slot0.kF = HOOD_KF;
    hoodConfig.slot0.kP = HOOD_KP;
    hoodConfig.slot0.kI = HOOD_KI;
    hoodConfig.slot0.kD = HOOD_KD;
    hoodConfig.slot0.closedLoopPeakOutput = 1;
    hoodConfig.openloopRamp = 0.5;
    hoodConfig.closedloopRamp = 0.5;
    hood.configAllSettings(hoodConfig);
    hood.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder);
    hood.setSensorPhase(true);
  }

  public void shoot() {
    Scheduler.get().scheduleCommand(new IndexBall(index));
  }

  public void shootManualDistance() {
    Scheduler.get().scheduleCommand(new IndexBall(index));
    // TODO: Add distance to shoot command
  }


  private double calculateSpeed(double distance, int hoodAngle) {
    if (hoodAngle == 0) { return 10;}
    if (hoodAngle == 1) { return 20;}
    if (hoodAngle == 2) { return 30;}
    if (hoodAngle == 3) { return 40;}
    return SHOOTER_IDLE_VELOCITY;
    
    // TODO: Equations
  }

  private int calculateHood(double distance) {
    if (distance > 36) { return 1;}
    if (distance > 20) { return 2;}
    if (distance > 10) { return 1;}
    return 0;
  }

  double lastHoodAngle = 0;
  
  @Override
  public void teleopPeriodic() {
    distance = 15;
    // distance = cameraTurret.getDistance;

    // angle = cameraTurret.getAngle;
    
    /* Hood control */
    // int hoodAngle = calculateHood(distance);
    double hoodAngle = Utils.clamp(ShuffleBoard.hoodPosition.getDouble(0), 0, 4);
    if (hoodAngle == 0 && lastHoodAngle != 0) {
      calibratingHood = true;
    }
    lastHoodAngle = hoodAngle;
    //System.out.println("Hood angle is " + hoodAngle);
    double targetHood = (hoodAngle / 3.0 * ROTS_PER_MIN_MAX * TICKS_PER_ROT) + 20;
    
    if (calibratingHood) {
      hood.set(ControlMode.PercentOutput, -0.2);
      System.out.println("Calibrating");

      if (hoodLimit.get()) {
        System.out.println("Calibrated!");
        calibratingHood = false;

        ShuffleBoard.hoodPosition.setDouble(0);
        hood.setSelectedSensorPosition(0);
      }
    } else {
      hood.set(ControlMode.Position, targetHood);
    }

    System.out.println(hood.getSelectedSensorPosition());

    // System.out.printf("Current: %3.3f Target: %3.3f %n", hood.getSelectedSensorPosition(), targetHood);

    if (input.getAim()) {
      flywheel.set(ControlMode.Velocity, ShuffleBoard.shooterFlywheelVelocity.getDouble(SHOOTER_IDLE_VELOCITY)/*calculateSpeed(distance, hoodAngle)*/);
      driveController.turnToTarget(0 /* cameraTurret.getAngle */);
    } else {
      flywheel.set(ControlMode.Velocity, SHOOTER_IDLE_VELOCITY);
    }
    
    if (input.getShoot()) {
      shoot();
    }
  }
}
