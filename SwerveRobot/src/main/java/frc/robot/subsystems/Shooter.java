package frc.robot.subsystems;

import frc.robot.control.Input;
import frc.robot.control.SwerveDriveController;

import edu.wpi.first.math.controller.SimpleMotorFeedforward;


public class Shooter extends Subsystem {
  private final Input input;
  private final SwerveDriveController driveController;
  private final SimpleMotorFeedforward feedForward;
 // private final CameraTurret cameraTurret;


  public Shooter(SwerveDriveController swerveDriveController/*, CameraTurret camera*/, Input input) {
    this.input = input;
    driveController = swerveDriveController;
    //cameraTurret = camera;
  }

  private void shoot(double distance) {

    /* Temporary */
    double speed = distance;
    
    double currentVelocity = motor.getSelectedSensorVelocity();
    double velocityDiff = speed - currentVelocity;
    double seconds = velocityDiff / INTAKE_MAX_SPEED;
    double percentOut = feedForward.calculate(currentVelocity, speed, seconds);

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
      driveController.turnToTarget(-50);
    }
  }
}
