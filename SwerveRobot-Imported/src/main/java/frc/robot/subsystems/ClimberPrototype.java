package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.control.Input;

import static frc.robot.Constants.*;

public class ClimberPrototype extends SubsystemBase {
  private final Input input;
  private final TalonSRX leftMotor;
  private final TalonSRX rightMotor;

  public ClimberPrototype() {
    input = new Input();
    leftMotor = new TalonSRX(CLIMBER_LEFT_MOTOR_ID);
    rightMotor = new TalonSRX(CLIMBER_LEFT_MOTOR_ID);
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
    double swingAmount = 0;
    if(input.getSwingLeft()){
      swingAmount--;
    }

    if(input.getSwingRight()){
      swingAmount++;
    }
    turnMotors(swingAmount);

  }

  // All commands periodic can run
  private void turnMotors(double percentAmount){
    leftMotor.set(ControlMode.PercentOutput,percentAmount);
    rightMotor.set(ControlMode.PercentOutput,percentAmount);
  }
}
