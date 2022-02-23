package frc.robot.command.climb;

import frc.robot.command.Command;
import frc.robot.subsystems.climber.TelescopingArm;

public class SetTelePosition implements Command {
  private final TelescopingArm arm;
  private final double height;
  public SetTelePosition(TelescopingArm arm, double height) {
    this.arm = arm;
    this.height = height;
  }

  @Override
  public boolean run() {
    arm.setPosition(height);
    return arm.isAtPosition();
  }
}
