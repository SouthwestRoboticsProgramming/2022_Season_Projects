package frc.robot.subsystems;

import edu.wpi.first.math.controller.SimpleMotorFeedforward;

import static frc.robot.Constants.*;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

// TODO: Feedforward control of flywheel speed
public class Intake extends Subsystem {

  private double speed;
  private final SimpleMotorFeedforward feedForward;
  private final TalonSRX motor;

  public Intake(int motorID) {
    feedForward = new SimpleMotorFeedforward(INTAKE_KS, INTAKE_KV, INTAKE_KA);
    motor = new TalonSRX(motorID);
    motor.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder);
  }

  public void setSpeed(double percentOfMax) {
    speed = percentOfMax * INTAKE_MAX_SPEED;
  }

  @Override
  public void teleopPeriodic() {
    double currentVelocity = motor.getSelectedSensorVelocity();
    feedForward.calculate(currentVelocity, speed, 0); //TODO: Check if this doesn't work

  }
}
