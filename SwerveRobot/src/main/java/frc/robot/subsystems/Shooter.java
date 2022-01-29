// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.control.Input;
import frc.robot.control.SwerveDriveController;

public class Shooter extends SubsystemBase {
  private static Input input;
  private static SwerveDriveController driveController;
  private Rotation2d targetAngle;
  /** Creates a new Shooter. */
  public Shooter(SwerveDriveController swerveDriveController) {
    input = new Input();
    driveController = swerveDriveController;
    targetAngle = new Rotation2d();
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run

    // Always be calculating where the target is

    /* 
    
    Get the angle of the camera turret and the angle of the gyro

    */

    if (input.getShoot()) {
      // Do the shooty shooty
      driveController.setRobotTargetAngle(targetAngle);
    }
  }
}
