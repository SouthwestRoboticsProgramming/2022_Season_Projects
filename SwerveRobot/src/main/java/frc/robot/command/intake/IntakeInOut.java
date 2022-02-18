package frc.robot.command.intake;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import frc.robot.command.Command;

import static frc.robot.constants.IntakeConstants.*;

public class IntakeInOut extends Command {
  private final TalonSRX motor;

  private int timer = 0;
  private boolean out;
  private boolean done = false;

  public IntakeInOut(TalonSRX motor, boolean out) {
    this.motor = motor;
    this.out = out;
  }

  @Override
  public boolean run() {
    if (out) {
      motor.set(ControlMode.PercentOutput, -0.15);
    } else {
      motor.set(ControlMode.PercentOutput, 0.15);
    }

    return ++timer >= INTAKE_TIME;
  }
}
