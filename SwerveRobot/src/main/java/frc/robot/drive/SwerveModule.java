package frc.robot.drive;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRXConfiguration;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.sensors.CANCoder;

import edu.wpi.first.wpilibj.controller.PIDController;
import frc.robot.util.Utils;

import static frc.robot.Constants.*;

// This was pretty much copied from the mini robot DriveTrain class,
// so it probably doesn't work.
// Wheel movements are commented out to prevent accidentally damaging the modules
public class SwerveModule {
    private final WPI_TalonSRX driveMotor;
    private final WPI_TalonSRX turnMotor;
    private final CANCoder canCoder;
    private final PIDController turnPID;

    private double currentAngle = 0;
    private double targetAngle = 0;
    private boolean flipDriveAmt = false;

    public SwerveModule(int drivePort, int turnPort, int canPort) {
        driveMotor = new WPI_TalonSRX(drivePort);
        turnMotor = new WPI_TalonSRX(turnPort);
        canCoder = new CANCoder(canPort);

        TalonSRXConfiguration config = new TalonSRXConfiguration();
        config.primaryPID.selectedFeedbackSensor = FeedbackDevice.QuadEncoder;
        config.neutralDeadband = 0.001;
        config.slot0.kF = 0;
        config.slot0.kP = 0;
        config.slot0.kI = 0;
        config.slot0.kD = 0;
        config.slot0.closedLoopPeakOutput = 1;
        config.openloopRamp = 0.5;
        config.closedloopRamp = 0.5;
        driveMotor.configAllSettings(config);
        turnMotor.configAllSettings(config);

        driveMotor.setNeutralMode(NeutralMode.Brake);
        driveMotor.setSelectedSensorPosition(0, 0, 30);
        driveMotor.stopMotor();

        turnMotor.setNeutralMode(NeutralMode.Brake);
        turnMotor.setSelectedSensorPosition(0, 0, 30);
        turnMotor.stopMotor();

        turnPID = new PIDController(WHEEL_TURN_KP, WHEEL_TURN_KI, WHEEL_TURN_KD);
        turnPID.enableContinuousInput(-Math.PI, Math.PI);
    }

    public void setTargetAngle(double angle) {
        targetAngle = angle;
    }

    public boolean isAtTargetAngle() {
        return Math.abs(currentAngle - targetAngle) < Math.toRadians(WHEEL_TURN_TOLERANCE);
    }

    public void drive(double amount) {
        amount = Utils.clamp(amount, -1, 1);
        if (flipDriveAmt) {
            amount = -amount;
        }
        
        //driveMotor.set(ControlMode.PercentOutput, amount);
    }

    public void update() {

        double rotPos = canCoder.getPosition();
        currentAngle = Math.toRadians(rotPos);

        double oppositeAngle = Utils.normalizeAngle(targetAngle + Math.PI);

        double normalDiff = Utils.normalizeAngle(currentAngle - targetAngle);
        double oppositeDiff = Utils.normalizeAngle(currentAngle - oppositeAngle);

        flipDriveAmt = normalDiff < oppositeDiff;
        double target = flipDriveAmt ? targetAngle : oppositeAngle;

        double amount = turnPID.calculate(currentAngle, target);
        amount = Utils.clamp(amount, -1, 1);
        turnMotor.set(ControlMode.PercentOutput, amount);
    }
}
