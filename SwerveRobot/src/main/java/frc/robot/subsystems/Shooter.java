package frc.robot.subsystems;

import frc.robot.control.Input;
import frc.robot.control.SwerveDriveController;

import edu.wpi.first.math.controller.SimpleMotorFeedforward;

import static frc.robot.constants.ShooterConstants.*;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonFX;


public class Shooter extends Subsystem {
  private final Input input;
  private final SwerveDriveController driveController;
  private final SimpleMotorFeedforward feedForward;
  private final CameraTurret cameraTurret;
  private final TalonFX motor;


  public Shooter(SwerveDriveController swerveDriveController, CameraTurret camera, Input input) {
    this.input = input;
    driveController = swerveDriveController;
    feedForward = new SimpleMotorFeedforward(SHOOTER_KS, SHOOTER_KV, SHOOTER_KA);
    cameraTurret = camera;
    motor = new TalonFX(SHOOTER_MOTOR_ID);
  }

  private void shoot(double distance) {
    /* Temporary */
    double speed = distance;

    double currentVelocity = motor.getSelectedSensorVelocity();
    double velocityDiff = speed - currentVelocity;
    double seconds = velocityDiff / SHOOTER_MAX_SPEED;
    double percentOut = feedForward.calculate(currentVelocity, speed, seconds);
    
    motor.set(ControlMode.PercentOutput, percentOut);
  }

  @Override
  public void robotPeriodic() {
    // This method will be called once per scheduler run

    // Always be calculating where the target is

    /* 
    
    Get the angle of the camera turret and the angle of the gyro

    */

    //double targetAngle = cameraTurret.getAngle();

    if (input.getAim()) {
      driveController.turnToTarget(30);
    }

    if (input.getShoot()) {
      // Do the shooty shooty

      //Testing
      driveController.turnToTarget(-50);

      shoot(50);

    }
  }
}
