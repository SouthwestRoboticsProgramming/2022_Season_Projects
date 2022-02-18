// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.command.intake;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj2.command.CommandBase;

import static frc.robot.constants.IntakeConstants.*;

public class IntakeInOut extends CommandBase {
  private final TalonSRX motor;

  private int time = 0;
  private boolean out;
  private boolean done = false;
  /** Creates a new intakeUp. */
  public IntakeInOut(TalonSRX motor) {
    // Use addRequirements() here to declare subsystem dependencies.
    this.motor = motor;
  }

  public void setOut(boolean out) {
    this.out = out;
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {}

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    while (time < INTAKE_TIME) {
      if (out) {
        motor.set(ControlMode.PercentOutput,-0.15);
      } else {
        motor.set(ControlMode.PercentOutput,0.15);
      }
    }
    done = true;
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {}

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return done;
  }
}
