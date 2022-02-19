package frc.robot.subsystems;

import frc.robot.Scheduler;
import frc.robot.command.shooter.IndexBall;
import frc.robot.control.Input;
import frc.robot.control.SwerveDriveController;
import frc.robot.util.ShuffleBoard;
import frc.robot.util.Utils;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.SimpleMotorFeedforward;

import static frc.robot.constants.ShooterConstants.*;


public class Shooter extends Subsystem {
  private final Input input;
  private final SwerveDriveController driveController;
  private SimpleMotorFeedforward feedForward;
  private final PIDController hoodPID;
  private final CameraTurret cameraTurret;
  private final TalonFX flywheel;
  private final TalonFX index;
  private final TalonSRX hood;

  //private final IndexBall indexBall;

  private double hoodTarget;
  private double speed = 0;


  private void recreateFeedForward() {
    feedForward = new SimpleMotorFeedforward(
      ShuffleBoard.flywheelKS.getDouble(SHOOTER_KS), 
      ShuffleBoard.flywheelKV.getDouble(SHOOTER_KV), 
      ShuffleBoard.flywheelKA.getDouble(SHOOTER_KA)
    );
  }

  public Shooter(SwerveDriveController swerveDriveController, CameraTurret camera, Input input) {
    this.input = input;
    driveController = swerveDriveController;
    
    hoodPID = new PIDController(HOOD_KP, HOOD_KI, HOOD_KD);
    recreateFeedForward();
    cameraTurret = camera;
    flywheel = new TalonFX(FLYWHEEL_MOTOR_ID);
    index = new TalonFX(INDEX_MOTOR_ID);
    hood = new TalonSRX(HOOD_MOTOR_ID);

    index.setInverted(true);
    flywheel.setInverted(true);

    //indexBall = new IndexBall(index);
  }

  public void shoot() {
    //indexBall.run();
    Scheduler.get().scheduleCommand(new IndexBall(index));
  }

  public void setDistance(double distance){

  }
  
  public void setHood(double angle) {
    hoodTarget = Utils.clamp(angle, MIN_ANGLE, MAX_ANGLE);
  }
  
  private boolean lastShoot = false;
  @Override
  public void robotPeriodic() {

    // TODO: Replace with distance equation
    speed = input.testShooterSpeed();
    setHood(input.testHoodAngle());
    
    //double targetAngle = cameraTurret.getAngle();

    // TODO: Find speed and andle based on distance
    
    /* Hood control */
    double currentHoodAngle = hood.getSelectedSensorPosition() * ROTS_PER_DEGREE + MIN_ANGLE;
    double hoodOut = hoodPID.calculate(currentHoodAngle, hoodTarget);

    // if (input.debug()) {
    //   recreateFeedForward();
    //   System.out.printf("Recreated feed forward: s %.3f v %.3f a %.3f %n", feedForward.ks, feedForward.kv, feedForward.ka);
    // }

    //feedForward = ShuffleBoard.flywheelKA.getDouble(SHOOTER_KA);
    
    double flywheelCurrentVelocity = flywheel.getSelectedSensorVelocity();
    double flywheelVelocityDiff = speed - flywheelCurrentVelocity;
    double flywheelSeconds = 1; //flywheelVelocityDiff / SHOOTER_MAX_ACCEL;
    double flywheelOut = feedForward.calculate(flywheelCurrentVelocity, speed, Math.abs(flywheelSeconds));
    //System.out.printf("cv: %3.3f diff: %3.3f sec: %3.3f out: %3.3f %n", flywheelCurrentVelocity, flywheelVelocityDiff, flywheelSeconds, flywheelOut);

    hood.set(ControlMode.PercentOutput, hoodOut);
    flywheel.set(ControlMode.PercentOutput, input.shoot() ? 0.75 : 0);
    
    if (input.getAim()) {
    }
    
    if (input.getShoot()) {
      // Do the shooty shooty
    }

    boolean shoot = input.testShoot();
    if (shoot && !lastShoot) {
      shoot();
    }
    lastShoot = shoot;
  }
}
