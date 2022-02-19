
package frc.robot.command.shooter;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonFX;

import frc.robot.command.Command;

import static frc.robot.constants.ShooterConstants.*;

public class IndexBall implements Command {
  private final TalonFX indexMotor;

  private int timer = 0;

  public IndexBall(TalonFX indexMotor) {
    this.indexMotor = indexMotor;

  }

  @Override
  public boolean run() {
    indexMotor.set(ControlMode.PercentOutput, INDEX_SPEED);
    
    boolean end = ++timer >= INDEX_TIME;

    if (end) {
      indexMotor.set(ControlMode.PercentOutput, 0);
      System.out.println("Index end");
    }

    return end;
  }

  
}
