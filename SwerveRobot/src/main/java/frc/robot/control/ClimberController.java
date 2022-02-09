package frc.robot.control;

import frc.robot.subsystems.Subsystem;
import frc.robot.subsystems.climber.SwingingArm;
import frc.robot.subsystems.climber.TelescopingArm;

import static frc.robot.constants.ClimberConstants.*;

public class ClimberController extends Subsystem {

  private final TelescopingArm teleLeft;
  private final TelescopingArm teleRight;
  private final SwingingArm swingLeft;
  private final SwingingArm swingRight;

  public ClimberController() {
    teleLeft = new TelescopingArm(CLIMBER_LEFT_TELE_MOTOR_ONE_ID, CLIMBER_LEFT_TELE_MOTOR_TWO_ID, CLIMBER_TELE_BASE_HEIGHT, CLIMBER_TELE_PULLEY_DIAMETER);
    teleRight = new TelescopingArm(CLIMBER_RIGHT_TELE_MOTOR_ONE_ID, CLIMBER_RIGHT_TELE_MOTOR_TWO_ID, CLIMBER_TELE_BASE_HEIGHT, CLIMBER_TELE_PULLEY_DIAMETER);
    swingLeft = new SwingingArm(CLIMBER_LEFT_SWING_MOTOR_ID, CLIMBER_SWING_ARM, CLIMBER_SWING_BASE, CLIMBER_SWING_DIST_PER_ROT);
    swingRight = new SwingingArm(CLIMBER_RIGHT_SWING_MOTOR_ID, CLIMBER_SWING_ARM,CLIMBER_SWING_BASE, CLIMBER_SWING_DIST_PER_ROT);
  }

  public void groundToSecond() {
    /* Sample Code */
    teleLeft.setPosition(50);
    teleRight.setPosition(50);

    swingLeft.swingToAngle(120);
    swingRight.swingToAngle(120);

    teleLeft.setPosition(0);
    teleRight.setPosition(0);
  }

  public void secondToThird() {}

  public void ThirdToFourth() {}

  @Override
  public void robotPeriodic() {
    // TODO: Use PID to keep arms where the should be if needed
  }
}
