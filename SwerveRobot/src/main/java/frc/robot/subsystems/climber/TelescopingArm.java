package frc.robot.subsystems.climber;

import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.math.controller.PIDController;
import frc.robot.subsystems.Subsystem;

import static frc.robot.constants.ClimberConstants.*;

public class TelescopingArm extends Subsystem {
  private final CANSparkMax motorOne, motorTwo;
  private final RelativeEncoder encoder;
  private final PIDController pid;

  private final double base, pulleyDiameter;

  public TelescopingArm(int motorOneID, int motorTwoID, double baseHeight, double pulleyDiameter) {
    motorOne = new CANSparkMax(motorOneID, MotorType.kBrushless);
    motorTwo = new CANSparkMax(motorOneID, MotorType.kBrushless);
    motorOne.setIdleMode(IdleMode.kBrake);
    motorTwo.setIdleMode(IdleMode.kBrake);
    pid = new PIDController(CLIMBER_TELE_MOTOR_KP, CLIMBER_TELE_MOTOR_KI, CLIMBER_TELE_MOTOR_KD);
    pid.setTolerance(CLIMBER_TELE_TOLERANCE);

    encoder = motorOne.getEncoder();

    this.base = baseHeight;
    this.pulleyDiameter = pulleyDiameter;
  }


  public void liftArm(double percent) {
    motorOne.set(percent);
    motorTwo.set(percent);
  }

  public void setPosition(double height) {

  /** 
   * @param height Distance from bottom of telescoping arm to top of the highest section
   */

   double currentPose = base + encoder.getPosition() * pulleyDiameter * Math.PI;
   double percentOut = pid.calculate(currentPose, height);
   motorOne.set(percentOut);
   motorTwo.set(percentOut);

  }

  public boolean isAtPosition() {
    return pid.atSetpoint();
  }

  @Override
  public void robotPeriodic() {
    // This method will be called once per scheduler run
  }
}
