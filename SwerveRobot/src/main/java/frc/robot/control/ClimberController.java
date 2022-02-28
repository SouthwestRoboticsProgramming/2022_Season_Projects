package frc.robot.control;

import frc.robot.subsystems.Subsystem;
import frc.robot.subsystems.climber.SwingingArm;
import frc.robot.subsystems.climber.TelescopingArm;
import frc.robot.util.Utils;
import frc.robot.control.Input;
import frc.robot.Scheduler;

import frc.robot.command.Command;
import frc.robot.command.CommandSequence;
import frc.robot.command.climb.SetArmRotation;

import static frc.robot.constants.ClimberConstants.*;

public class ClimberController extends Subsystem {

  private final TelescopingArm teleLeft;
  private final TelescopingArm teleRight;
  private final SwingingArm swingLeft;
  private final SwingingArm swingRight;

  private final Input input;

  public ClimberController(Input input) {
    teleLeft = new TelescopingArm(CLIMBER_LEFT_TELE_MOTOR_ONE_ID, CLIMBER_LEFT_TELE_MOTOR_TWO_ID, CLIMBER_TELE_BASE_HEIGHT, CLIMBER_TELE_PULLEY_DIAMETER, true);
    teleRight = new TelescopingArm(CLIMBER_RIGHT_TELE_MOTOR_ONE_ID, CLIMBER_RIGHT_TELE_MOTOR_TWO_ID, CLIMBER_TELE_BASE_HEIGHT, CLIMBER_TELE_PULLEY_DIAMETER, false);
    swingLeft = new SwingingArm(CLIMBER_LEFT_SWING_MOTOR_ID);
     swingRight = new SwingingArm(CLIMBER_RIGHT_SWING_MOTOR_ID);
    this.input = input;
  }

  public void groundToSecond() {

    // Scheduler.get().scheduleCommand(new SetArmRotation(swingLeft, CLIMBER_ARM_OUT_OF_THE_WAY));
    // Scheduler.get().scheduleCommand(new SetArmRotation(swingRight, CLIMBER_ARM_OUT_OF_THE_WAY));

    teleLeft.liftArm(Utils.clamp(input.getClimbTele(),-CLIMBER_MAX_SPEED, CLIMBER_MAX_SPEED));
    teleRight.liftArm(Utils.clamp(input.getClimbTele(),-CLIMBER_MAX_SPEED, CLIMBER_MAX_SPEED));


    // // // // // // // /*// // // */ // teleLeft.setPosition(36);
    //teleRight.setPosition(46);

    if (input.getClimbNextStep()) {
      groundToSecondII();
    } else {
      swingLeft.swingToAngle(100);
      swingRight.swingToAngle(100);
    }
  }

  private void groundToSecondII() {
    //System.out.println("Onto the second half of ground to second");

    swingLeft.swingToAngle(65);
    swingRight.swingToAngle(65);
  }

  public void secondToThird() {}

  public void ThirdToFourth() {}

  @Override
  public void robotPeriodic() {
    // TODO: Use PID to keep arms where the should be if needed
  }
}
