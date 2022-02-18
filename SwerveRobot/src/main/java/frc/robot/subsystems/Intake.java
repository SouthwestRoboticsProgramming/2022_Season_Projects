package frc.robot.subsystems;

import edu.wpi.first.math.controller.SimpleMotorFeedforward;

import frc.robot.command.intake.IntakeInOut;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import static frc.robot.constants.IntakeConstants.*;

// TODO: Feedforward control of flywheel speed
public class Intake extends Subsystem {

  private double speed;
  private boolean isDown;
  private final SimpleMotorFeedforward feedForward;
  private final TalonFX motor;
  private final TalonSRX lift;
  private final IntakeInOut liftControl;

  public Intake(int motorID, int liftMotorID) {
    feedForward = new SimpleMotorFeedforward(INTAKE_KS, INTAKE_KV, INTAKE_KA);
    motor = new TalonFX(motorID);

    lift = new TalonSRX(liftMotorID);
    lift.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder);

    liftControl = new IntakeInOut(lift);

    isDown = false;
  }

  public void setSpeed(double percentOfMax) {
    speed = percentOfMax * INTAKE_MAX_SPEED;
  }

  public void intakeDown() {
    if (isDown) { return; }
    isDown = true;
    liftControl.setOut(true);
    liftControl.execute();

  }

  public void intakeUp() {
    if (!isDown){ return; }
    isDown = false;
    liftControl.setOut(false);
    liftControl.execute();

  }

  @Override
  public void teleopPeriodic() {

    if(isDown){
      double currentVelocity = motor.getSelectedSensorVelocity();
      double velocityDiff = speed - currentVelocity;
      double seconds = velocityDiff / INTAKE_MAX_SPEED;
      double motorOut = feedForward.calculate(currentVelocity, speed, seconds);
      motor.set(ControlMode.Velocity,motorOut);
      System.out.println(motorOut);
    }

  }
}
