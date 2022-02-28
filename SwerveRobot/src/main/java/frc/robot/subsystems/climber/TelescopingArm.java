package frc.robot.subsystems.climber;

import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.math.controller.PIDController;
import frc.robot.subsystems.Subsystem;
import frc.robot.util.Utils;

import static frc.robot.constants.ClimberConstants.*;

public class TelescopingArm extends Subsystem {
  private final CANSparkMax motorOne, motorTwo;
  private final RelativeEncoder encoder;
  private final PIDController pid;

  private final double base, pulleyDiameter;

  public TelescopingArm(int motorOneID, int motorTwoID, double baseHeight, double pulleyDiameter, boolean inverted) {
    motorOne = new CANSparkMax(motorOneID, MotorType.kBrushless);
    motorTwo = new CANSparkMax(motorTwoID, MotorType.kBrushless);
    motorOne.setIdleMode(IdleMode.kBrake);
    motorTwo.setIdleMode(IdleMode.kBrake);
    pid = new PIDController(CLIMBER_TELE_MOTOR_KP, CLIMBER_TELE_MOTOR_KI, CLIMBER_TELE_MOTOR_KD);
    pid.setTolerance(CLIMBER_TELE_TOLERANCE);

    motorOne.setInverted(inverted);
    motorTwo.setInverted(inverted);

    encoder = motorOne.getEncoder();
    encoder.setPosition(0); // zero it

    this.base = baseHeight;
    this.pulleyDiameter = pulleyDiameter;
  }


  public void liftArm(double percent) {
    motorOne.set(percent);
    motorTwo.set(percent);
    //System.out.println(encoder.getPosition());
  }

  public void setPosition(double height) {

  /** 
   * @param height Distance from bottom of telescoping arm to top of the highest section
   */

   double currentPose = base + (encoder.getPosition() / CLIMBER_TELE_TICKS_PER_PULLEY )* pulleyDiameter * Math.PI;
   double percentOut = Utils.clamp(pid.calculate(currentPose, height),-CLIMBER_MAX_SPEED,CLIMBER_MAX_SPEED);
   motorOne.set(percentOut);
   motorTwo.set(percentOut);
   System.out.println(currentPose);
   System.out.println("Ticks: " + encoder.getPosition());

  }

  public void stopMotors() {
    motorOne.stopMotor();
    motorTwo.stopMotor();
  }

  public boolean isAtPosition() {
    return pid.atSetpoint();
  }

  @Override
  public void robotPeriodic() {
    // This method will be called once per scheduler run
    System.out.println("The encoder is at " + encoder.getPosition());

    
  }

  @Override
  public void disabledInit() {
    encoder.setPosition(0);
    stopMotors();
  }
}
