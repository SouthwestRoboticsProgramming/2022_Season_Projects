package frc.robot.control;

import frc.robot.subsystems.Subsystem;
import frc.robot.subsystems.climber.SwingingArm;
import frc.robot.subsystems.climber.TelescopingArm;
import frc.robot.util.Utils;
import frc.robot.control.Input;

import static frc.robot.constants.ClimberConstants.*;

public class ClimberController extends Subsystem {

  private final TelescopingArm teleLeft;
  private final TelescopingArm teleRight;
  private final SwingingArm swingLeft;
  private final SwingingArm swingRight;

  private final Input input;

  public ClimberController(Input input) {
    teleLeft = new TelescopingArm(CLIMBER_LEFT_TELE_MOTOR_ONE_ID, CLIMBER_LEFT_TELE_MOTOR_TWO_ID, CLIMBER_TELE_BASE_HEIGHT, CLIMBER_TELE_PULLEY_DIAMETER);
    teleRight = new TelescopingArm(CLIMBER_RIGHT_TELE_MOTOR_ONE_ID, CLIMBER_RIGHT_TELE_MOTOR_TWO_ID, CLIMBER_TELE_BASE_HEIGHT, CLIMBER_TELE_PULLEY_DIAMETER);
    swingLeft = new SwingingArm(CLIMBER_LEFT_SWING_MOTOR_ID);
    swingRight = new SwingingArm(CLIMBER_RIGHT_SWING_MOTOR_ID);
    this.input = input;
  }

  public void groundToSecond() {
    teleLeft.liftArm(Utils.clamp(input.getClimbTele(),-CLIMBER_MAX_SPEED, CLIMBER_MAX_SPEED));
    teleRight.liftArm(Utils.clamp(input.getClimbTele(),-CLIMBER_MAX_SPEED, CLIMBER_MAX_SPEED));

    if (input.getClimbNextStep()) {
      groundToSecondII();
    }
  }

  private void groundToSecondII() {
    
  }

  public void secondToThird() {}

  public void ThirdToFourth() {}

  @Override
  public void robotPeriodic() {
    // TODO: Use PID to keep arms where the should be if needed

  }
}
