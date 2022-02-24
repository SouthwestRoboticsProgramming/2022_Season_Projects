package frc.robot.command.climb;

import frc.robot.command.Command;
import frc.robot.subsystems.climber.SwingingArm;

public class SetArmRotation implements Command {

  private final SwingingArm arm;
  private final double angle;

  public SetArmRotation(SwingingArm arm, double angle) {
    this.arm = arm;
    this.angle = angle;
  }

  @Override
  public boolean run() {
    arm.swingToAngle(angle);
    return arm.isAtAngle();
  }
}
