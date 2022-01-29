// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import static frc.robot.Constants.*;

public class CameraTurret extends SubsystemBase {
  /** Creates a new CameraTurret. */
  private final PIDController pid;
  private final Servo turretServo;

  public CameraTurret() {
    pid = new PIDController(CAMERA_TURRET_KP, CAMERA_TURRET_KI, CAMERA_TURRET_KD);
    turretServo = new Servo(CAMERA_TURRET_SERVO_ID);
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
    //turretServo.setAngle();
  }
}
