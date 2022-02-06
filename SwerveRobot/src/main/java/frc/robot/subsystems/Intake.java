package frc.robot.subsystems;

import edu.wpi.first.math.controller.SimpleMotorFeedforward;

import static frc.robot.Constants.*;

// TODO: Feedforward control of flywheel speed
public class Intake extends Subsystem {
  SimpleMotorFeedforward feedForward;

  public Intake() {
    feedForward = new SimpleMotorFeedforward(INTAKE_KS, INTAKE_KV, INTAKE_KA);
  }

  @Override
  public void robotPeriodic() {

  }
}
