package frc.robot.subsystems.climber;

import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.math.controller.PIDController;
import static frc.robot.Constants.*;
import frc.robot.subsystems.Subsystem;

public class SwingingArm extends Subsystem {

  private static CANSparkMax motor;
  private static RelativeEncoder encoder;
  private static PIDController pid;

  public SwingingArm(int motorID) {
    motor = new CANSparkMax(motorID, MotorType.kBrushless);
    encoder = motor.getEncoder();
    encoder.setPositionConversionFactor(360);
    pid = new PIDController(CLIMBER_SWING_MOTOR_KP, CLIMBER_SWING_MOTOR_KI, CLIMBER_SWING_MOTOR_KD);
  }

  public void swingToAngle(double angle) {

    // TODO: Do the math required here to find the actual angle
    
    // double currentAngle = encoder.getPosition();
    // double percentOutput = pid.calculate(currentAngle, angle);
    // motor.set(percentOutput);
    
  }

  @Override
  public void robotPeriodic() {
    // This method will be called once per scheduler run
  }
}
