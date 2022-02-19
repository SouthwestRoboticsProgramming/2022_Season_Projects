package frc.robot.subsystems;

import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import frc.robot.Scheduler;
import frc.robot.command.intake.IntakeDown;
import frc.robot.command.intake.IntakeUp;
import frc.robot.control.Input;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.motorcontrol.can.TalonFXConfiguration;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import static frc.robot.constants.IntakeConstants.*;

public class Intake extends Subsystem {

  private final Input input;
  private final SimpleMotorFeedforward feedForward;
  private final TalonFX motor;
  private final TalonSRX lift;
  
  private double speed;
  private boolean isDown;

  public Intake(Input input) {
    this.input = input;
    feedForward = new SimpleMotorFeedforward(INTAKE_KS, INTAKE_KV, INTAKE_KA);
    motor = new TalonFX(INTAKE_MOTOR_ID);
    motor.setInverted(true);

    TalonFXConfiguration config = new TalonFXConfiguration();
    config.neutralDeadband = 0.001;
    config.slot0.kF = 0;
    config.slot0.kP = 0.1;
    config.slot0.kI = 0;
    config.slot0.kD = 0;
    config.slot0.closedLoopPeakOutput = 1;
    config.openloopRamp = 0.5;
    config.closedloopRamp = 0.5;
    motor.configAllSettings(config);
    motor.configSelectedFeedbackSensor(FeedbackDevice.IntegratedSensor);
    
    lift = new TalonSRX(INTAKE_LIFT_ID);
    lift.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder);
    
    isDown = false;
    
    speed = 0;
  }
  
  public void setSpeed(double percentOfMax) {
    speed = percentOfMax * INTAKE_MAX_SPEED;
  }
  
  public void intakeDown() {
    if (isDown) { return; }
    isDown = true;
    
    Scheduler.get().scheduleCommand(new IntakeDown(lift));
  }
  
  public void intakeUp() {
    if (!isDown){ return; }
    isDown = false;
    
    Scheduler.get().scheduleCommand(new IntakeUp(lift));
  }
  
  private boolean lastUp = false, lastDown = false;
  @Override
  public void teleopPeriodic() {
    
    if(isDown){
      double currentVelocity = motor.getSelectedSensorPosition();
      double velocityDiff = speed - currentVelocity;
      double seconds = velocityDiff / INTAKE_MAX_SPEED;
      double motorOut = feedForward.calculate(currentVelocity, speed, seconds);
      // motor.set(ControlMode.Velocity,motorOut);
      System.out.println("Intake Lift Motor Out: " + motorOut);
    }
    
    if (input.intake()) {
      motor.set(ControlMode.Position, 2000);
    } else {
      motor.set(ControlMode.Position, 0);
    }
    System.out.println(motor.getSelectedSensorPosition());
    
    boolean up = input.testIntakeLiftUp();
    boolean down = input.testIntakeLiftDown();
    
    if (up && !lastUp) {
      intakeUp();
    }
    
    if (down && !lastDown) {
      intakeDown();
    }
    
    lastUp = up;
    lastDown = down;
    
    setSpeed(input.testIntakeSpeed());
  }
}
