package frc.robot.subsystems.climber_parts;

import edu.wpi.first.wpilibj.motorcontrol.Spark;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class TelescopingArm extends SubsystemBase {

  public static Spark motorOne;
  public static Spark motorTwo;

  public TelescopingArm(int motorOneID, int morotTwoID) {
    motorOne = new Spark(motorOneID);
    motorTwo = new Spark(motorOneID);
  }


  public void liftArm(double percent) {
    motorOne.set(percent);
    motorTwo.set(percent);
  }

  public void setPosition(double position)


  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }
}
