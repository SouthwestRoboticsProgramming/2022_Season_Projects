package frc.robot.subsystems.climber;

import edu.wpi.first.math.controller.PIDController;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.revrobotics.RelativeEncoder;

import static frc.robot.constants.ClimberConstants.*;

public class NewSwingingArm {
    private final CANSparkMax motor;
    private final RelativeEncoder encoder;
    
    private final PIDController pid;

    public NewSwingingArm(int motorID) {
        motor = new CANSparkMax(motorID, MotorType.kBrushless);
        motor.setIdleMode(IdleMode.kBrake);
        motor.setInverted(true);

        encoder = motor.getEncoder();
        encoder.setPosition(0);
        
        pid = new PIDController(
            CLIMBER_SWING_MOTOR_KP,
            CLIMBER_SWING_MOTOR_KI,
            CLIMBER_SWING_MOTOR_KD
        );
        pid.setTolerance(CLIMBER_SWING_TOLERANCE);
    }

    public void swingToAngle(double degrees) {
        double currentPose = encoder.getPosition() / CLIMBER_SWING_ROTS_PER_INCH + CLIMBER_STARTING_DIST;
        double currentAngle = Math.acos(
            (
                CLIMBER_SWING_BASE * CLIMBER_SWING_BASE 
                + CLIMBER_SWING_ARM * CLIMBER_SWING_ARM 
                - currentPose * currentPose
            ) / (2 * CLIMBER_SWING_ARM * CLIMBER_SWING_BASE)
        );
    
        double out = pid.calculate(Math.toDegrees(currentAngle), degrees);
        motor.set(out);
    }
}
