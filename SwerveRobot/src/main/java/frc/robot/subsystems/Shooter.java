// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.control.Input;
import frc.robot.control.SwerveDriveController;

public class Shooter extends Subsystem {
  private final Input input;
  private final SwerveDriveController driveController;
 // private final CameraTurret cameraTurret;


  public Shooter(SwerveDriveController swerveDriveController/*, CameraTurret camera*/, Input input) {
    this.input = input;
    driveController = swerveDriveController;
    //cameraTurret = camera;
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
