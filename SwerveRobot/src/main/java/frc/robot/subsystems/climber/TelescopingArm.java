package frc.robot.subsystems.climber;

import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class TelescopingArm extends SubsystemBase {

  public static CANSparkMax motorOne;
  public static CANSparkMax motorTwo;
  public static RelativeEncoder motorOneEncoder;
  public static RelativeEncoder motorTwoEncoder;

  public TelescopingArm(int motorOneID, int motorTwoID) {
    motorOne = new CANSparkMax(motorOneID, MotorType.kBrushless);
    motorTwo = new CANSparkMax(motorOneID, MotorType.kBrushless);

    motorOneEncoder = motorOne.getEncoder();
    motorTwoEncoder = motorTwo.getEncoder();
  }


  public void liftArm(double percent) {
    motorOne.set(percent);
    motorTwo.set(percent);
  }

  public void setPosition(double position) {

  }

  public void resetToBottom() {
     double motorOnespeed = motorOneEncoder.getVelocity();
     double motorTwoSpeed = motorTwoEncoder.getVelocity();
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }
}
