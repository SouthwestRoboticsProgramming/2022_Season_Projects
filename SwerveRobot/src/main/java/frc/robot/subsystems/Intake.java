package frc.robot.subsystems;

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
  private final TalonFX motor;
  private final TalonSRX lift;
  
  private boolean isDown;

  public Intake(Input input) {
    this.input = input;
    motor = new TalonFX(INTAKE_MOTOR_ID);
    motor.setInverted(true);

    TalonFXConfiguration config = new TalonFXConfiguration();
    config.neutralDeadband = 0.001;
    config.slot0.kF = INTAKE_KF;
    config.slot0.kP = INTAKE_KP;
    config.slot0.kI = INTAKE_KI;
    config.slot0.kD = INTAKE_KD;
    config.slot0.closedLoopPeakOutput = 1;
    config.openloopRamp = 0.5;
    config.closedloopRamp = 0.5;
    motor.configAllSettings(config);
    motor.configSelectedFeedbackSensor(FeedbackDevice.IntegratedSensor);
    
    lift = new TalonSRX(INTAKE_LIFT_ID);
    lift.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder);
    
    isDown = false;
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


  @Override
  public void teleopPeriodic() {
    
    if (input.getIntake() & !input.getIntakeLift()) {
      intakeDown();
      motor.set(ControlMode.Velocity, INTAKE_FULL_VELOCITY);
    } else if (!input.getIntakeLift()){
      intakeDown();
      motor.set(ControlMode.Velocity, INTAKE_NEUTRAL_VELOCITY);
    } else {
      intakeUp();
      motor.set(ControlMode.Velocity, 0);
    }

  }
}
