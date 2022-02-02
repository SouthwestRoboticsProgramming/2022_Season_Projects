package frc.robot.control;

import static frc.robot.Constants.*;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.subsystems.climber.SwingingArm;
import frc.robot.subsystems.climber.TelescopingArm;

public class ClimberController extends SubsystemBase {

  private static TelescopingArm teleLeft;
  private static TelescopingArm teleRight;
  private static SwingingArm swingLeft;
  private static SwingingArm swingRight;

  public ClimberController() {
    teleLeft = new TelescopingArm(CLIMBER_LEFT_TELE_MOTOR_ONE_ID, CLIMBER_LEFT_TELE_MOTOR_TWO_ID);
    teleRight = new TelescopingArm(CLIMBER_RIGHT_TELE_MOTOR_ONE_ID, CLIMBER_RIGHT_TELE_MOTOR_TWO_ID);
    swingLeft = new SwingingArm(CLIMBER_LEFT_SWING_MOTOR_ID);
    swingRight = new SwingingArm(CLIMBER_RIGHT_SWING_MOTOR_ID);
  }

  public void groundToSecond() {}

  public void secondToThird() {}

  public void ThirdToFourth() {}

  @Override
  public void periodic() {
    // TODO: Use PID to keep arms where the should be if needed
  }
}
