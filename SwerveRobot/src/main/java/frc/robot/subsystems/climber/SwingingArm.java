package frc.robot.subsystems.climber;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import frc.robot.subsystems.Subsystem;

public class SwingingArm extends Subsystem {

  private static CANSparkMax motor;

  public SwingingArm(int motorID) {
    motor = new CANSparkMax(motorID, MotorType.kBrushless);
  }

  @Override
  public void robotPeriodic() {
    // This method will be called once per scheduler run
  }
}
