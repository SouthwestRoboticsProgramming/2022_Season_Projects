package frc.robot.subsystems;

import frc.robot.command.shooter.IndexBall;
import frc.robot.control.Input;
import frc.robot.control.SwerveDriveController;
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
  private final SimpleMotorFeedforward feedForward;
  private final PIDController hoodPID;
  private final CameraTurret cameraTurret;
  private final TalonFX flywheel;
  private final TalonFX index;
  private final TalonSRX hood;

  private final IndexBall indexBall;

  private double hoodTarget;
  private double speed = 0;


  public Shooter(SwerveDriveController swerveDriveController, CameraTurret camera, Input input, int flyWheelID, int hoodControlID, int indexID) {
    this.input = input;
    driveController = swerveDriveController;
    feedForward = new SimpleMotorFeedforward(SHOOTER_KS, SHOOTER_KV);
    hoodPID = new PIDController(HOOD_KP, HOOD_KI, HOOD_KD);

    cameraTurret = camera;
    flywheel = new TalonFX(flyWheelID);
    index = new TalonFX(indexID);
    hood = new TalonSRX(hoodControlID);

    indexBall = new IndexBall(index);
  }

  public void shoot() {
    indexBall.run();
  }

  public void setDistance(double distance){

  }
  
  public void setHood(double angle) {
    hoodTarget = Utils.clamp(angle, MIN_ANGLE, MAX_ANGLE);
  }
  
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
    
    double flywheelCurrentVelocity = flywheel.getSelectedSensorVelocity();
    double flywheelVelocityDiff = speed - flywheelCurrentVelocity;
    double flywheelSeconds = flywheelVelocityDiff / SHOOTER_MAX_ACCEL;
    double flywheelOut = feedForward.calculate(flywheelCurrentVelocity, speed, flywheelSeconds);

    hood.set(ControlMode.PercentOutput, hoodOut);
    flywheel.set(ControlMode.Velocity, flywheelOut);
    
    if (input.getAim()) {
    }
    
    if (input.getShoot()) {
      // Do the shooty shooty
    }

    if (input.testShoot()) {
      shoot();
    }
  }
}
