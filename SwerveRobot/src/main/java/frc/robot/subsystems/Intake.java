package frc.robot.subsystems;

import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import static frc.robot.Constants.*;

// TODO: Feedforward control of flywheel speed


public class Intake extends SubsystemBase {
  SimpleMotorFeedforward feedForward;

  public Intake() {
    feedForward = new SimpleMotorFeedforward(INTAKE_KS, INTAKE_KV, INTAKE_KA);
  }

  @Override
  public void periodic() {

  }
}
