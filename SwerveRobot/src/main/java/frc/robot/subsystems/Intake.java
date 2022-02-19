package frc.robot.subsystems;

import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import frc.robot.command.intake.IntakeDown;
import frc.robot.command.intake.IntakeUp;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import static frc.robot.constants.IntakeConstants.*;

public class Intake extends Subsystem {

  private double speed;
  private boolean isDown;
  private final SimpleMotorFeedforward feedForward;
  private final TalonFX motor;
  private final TalonSRX lift;
  private final IntakeUp liftControlUp;
  private final IntakeDown liftControlDown;

  public Intake(int motorID, int liftMotorID) {
    feedForward = new SimpleMotorFeedforward(INTAKE_KS, INTAKE_KV, INTAKE_KA);
    motor = new TalonFX(motorID);

    lift = new TalonSRX(liftMotorID);
    lift.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder);

    liftControlUp = new IntakeUp(lift);
    liftControlDown = new IntakeDown(lift);

    isDown = false;
  }

  public void setSpeed(double percentOfMax) {
    speed = percentOfMax * INTAKE_MAX_SPEED;
  }

  public void intakeDown() {
    if (isDown) { return; }
    isDown = true;
    liftControlDown.run();

  }

  public void intakeUp() {
    if (!isDown){ return; }
    isDown = false;
    liftControlUp.run();

  }

  @Override
  public void teleopPeriodic() {

    if(isDown){
      double currentVelocity = motor.getSelectedSensorVelocity();
      double velocityDiff = speed - currentVelocity;
      double seconds = velocityDiff / INTAKE_MAX_SPEED;
      double motorOut = feedForward.calculate(currentVelocity, speed, seconds);
      motor.set(ControlMode.Velocity,motorOut);
      System.out.println("Intake Lift Motor Out: " + motorOut);
    }

  }
}
