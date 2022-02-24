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

  private final double arm, base, rotsPerInch;

  public SwingingArm(int motorID) {
    motor = new CANSparkMax(motorID, MotorType.kBrushless);
    encoder = motor.getEncoder();
    pid = new PIDController(CLIMBER_SWING_MOTOR_KP, CLIMBER_SWING_MOTOR_KI, CLIMBER_SWING_MOTOR_KD);
    pid.setTolerance(CLIMBER_SWING_TOLERANCE);

    motor.setIdleMode(IdleMode.kBrake);
    motor.setInverted(false);
    
    arm = CLIMBER_SWING_ARM;
    base = CLIMBER_SWING_BASE;
    rotsPerInch = CLIMBER_SWING_ROTS_PER_INCH;
  }

  public void swingToAngle(double degrees) {
    double currentPose = encoder.getPosition() / rotsPerInch + CLIMBER_STARTING_DIST;
    double currentAngle = Math.acos((base*base + arm*arm - currentPose*currentPose)/(2*arm*base));

    double percentOut = pid.calculate(Math.toDegrees(currentAngle), degrees);
    motor.set(percentOut);
  }

  public boolean isAtAngle() {
    return pid.atSetpoint();
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
