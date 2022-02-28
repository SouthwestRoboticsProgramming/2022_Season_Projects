package frc.robot.subsystems.climber;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.revrobotics.RelativeEncoder;

import edu.wpi.first.math.controller.PIDController;
import frc.robot.util.ShuffleBoard;
import frc.robot.util.Utils;

import static frc.robot.constants.ClimberConstants.*;

public class NewTelescopingArm {
    private static final int MIN_TICKS = 0;
    private static final int MAX_TICKS = 46;

    private final CANSparkMax motor1, motor2;
    private final RelativeEncoder encoder;

    private final PIDController heightPID;

    public NewTelescopingArm(int motor1ID, int motor2ID, boolean inverted) {
        motor1 = new CANSparkMax(motor1ID, MotorType.kBrushless);
        motor1.setIdleMode(IdleMode.kBrake);
        motor1.setInverted(inverted);

        motor2 = new CANSparkMax(motor2ID, MotorType.kBrushless);
        motor2.setIdleMode(IdleMode.kBrake);
        motor2.setInverted(inverted);

        encoder = motor1.getEncoder();
        encoder.setPosition(0);

        heightPID = new PIDController(
            CLIMBER_TELE_MOTOR_KP,
            CLIMBER_TELE_MOTOR_KI,
            CLIMBER_TELE_MOTOR_KD
        );
        heightPID.setTolerance(CLIMBER_TELE_TOLERANCE);
    }

    // Distance 0 to 1
    public void extendToDistance(double distance) {
        // Tune PID from shuffleboard
        heightPID.setPID(
            ShuffleBoard.climberTelescopeKP.getDouble(CLIMBER_TELE_MOTOR_KP),
            ShuffleBoard.climberTelescopeKI.getDouble(CLIMBER_TELE_MOTOR_KI),
            ShuffleBoard.climberTelescopeKD.getDouble(CLIMBER_TELE_MOTOR_KD)
        );

        // Map to range MIN_TICKS to MAX_TICKS
        distance = Utils.map(distance, 0, 1, MIN_TICKS, MAX_TICKS);

        // Calculate PID
        double pid = heightPID.calculate(encoder.getPosition(), distance);
        System.out.println("PID stuff: " + encoder.getPosition() + " -> " + distance + " (" + pid + ")");

        // Run motors
        motor1.set(pid);
        motor2.set(pid);

        // if (heightPID.atSetpoint()) {
        //     motor1.stopMotor();
        //     motor2.stopMotor();
        // }
    }

    public void manualMove(double speed) {
        motor1.set(speed);
        motor2.set(speed);
    }

    public void stop() {
        motor1.stopMotor();
        motor2.stopMotor();
    }
}
