package frc.robot.control;

import frc.robot.subsystems.Subsystem;
import frc.robot.subsystems.climber.SwingingArm;
import frc.robot.subsystems.climber.TelescopingArm;

import static frc.robot.Constants.*;

public class ClimberController extends Subsystem {

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
  public void robotPeriodic() {
    // TODO: Use PID to keep arms where the should be if needed
  }
}
