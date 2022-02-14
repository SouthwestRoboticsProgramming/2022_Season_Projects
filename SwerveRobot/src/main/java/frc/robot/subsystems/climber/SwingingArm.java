package frc.robot.subsystems.climber;

import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.math.controller.PIDController;
import frc.robot.subsystems.Subsystem;

import static frc.robot.constants.ClimberConstants.*;

public class SwingingArm extends Subsystem {

  private final CANSparkMax motor;
  private final RelativeEncoder encoder;
  private final PIDController pid;

  private final double arm, base, distPerRot;

  public SwingingArm(int motorID, double armLength, double pivotToMotor, double distancePerRotation) {
    motor = new CANSparkMax(motorID, MotorType.kBrushless);
    encoder = motor.getEncoder();
    pid = new PIDController(CLIMBER_SWING_MOTOR_KP, CLIMBER_SWING_MOTOR_KI, CLIMBER_SWING_MOTOR_KD);

    motor.setIdleMode(IdleMode.kBrake);
    
    arm = armLength;
    base = pivotToMotor;
    distPerRot = distancePerRotation;
  }

  public void swingToAngle(double angle) {
    double currentPose = encoder.getPosition() * distPerRot;
    double currentAngle = (base*base + arm*arm - currentPose);

    double percentOut = pid.calculate(currentAngle, angle);
    motor.set(percentOut);
  }

  @Override
  public void robotPeriodic() {
    
  }

  @Override
  public void disabledPeriodic() {
    pid.reset();
    motor.stopMotor();
  }
}
