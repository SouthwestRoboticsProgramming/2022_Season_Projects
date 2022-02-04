package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

// TODO: Feedforward control of flywheel speed


public class Intake extends SubsystemBase {
  SimpleMotorFeedForward feedForward;

  public Intake() {
    feedForward = new SimpleMotorFeedForward(ks,kv,ka)
  }

  @Override
  public void periodic() {

  }
}
