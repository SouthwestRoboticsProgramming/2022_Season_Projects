package frc.robot.drive;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.RemoteSensorSource;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.motorcontrol.can.TalonFXConfiguration;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.TalonSRXConfiguration;
import com.ctre.phoenix.sensors.AbsoluteSensorRange;
import com.ctre.phoenix.sensors.CANCoder;
import com.ctre.phoenix.sensors.CANCoderConfiguration;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import frc.robot.util.ShuffleBoard;
import frc.robot.util.Utils;

import static frc.robot.constants.DriveConstants.*;

public class SwerveModule {
    private final TalonFX driveMotor;
    private final TalonSRX turnMotor;
    private final CANCoder canCoder;

    private final boolean isDebug;

    public SwerveModule(int driveID, int turnID, int encoderID, double encoderOffset) {
        driveMotor = new TalonFX(driveID, GERALD);
        turnMotor = new TalonSRX(turnID);
        canCoder = new CANCoder(encoderID, GERALD);

        isDebug = encoderID == SWERVE_MODULES[0].getCanCoderId();

        // Set up drive motor
        TalonFXConfiguration driveConfig = new TalonFXConfiguration();
        // Configure the config
        driveMotor.configAllSettings(driveConfig);
        driveMotor.setSelectedSensorPosition(0, 0, 30);
        driveMotor.setNeutralMode(NeutralMode.Brake);

        // Set up turn motor
        TalonSRXConfiguration turnConfig = new TalonSRXConfiguration();
        {
            // Use the CANCoder as the position sensor
            // turnConfig.primaryPID.selectedFeedbackSensor = FeedbackDevice.SoftwareEmulatedSensor;

            // Set the PID constants
            turnConfig.neutralDeadband = 0.001;
            turnConfig.slot0.kP = WHEEL_TURN_KP;
            turnConfig.slot0.kI = WHEEL_TURN_KI;
            turnConfig.slot0.kD = WHEEL_TURN_KD;
            turnConfig.slot0.closedLoopPeakOutput = 1;
            turnConfig.openloopRamp = 0.5;
            turnConfig.closedloopRamp = 0.5;
        }
        turnMotor.configAllSettings(turnConfig);
        turnMotor.configSelectedFeedbackSensor(FeedbackDevice.SoftwareEmulatedSensor);
        turnMotor.setNeutralMode(NeutralMode.Brake);

        // Set up CANCoder
        CANCoderConfiguration encoderConfig = new CANCoderConfiguration();
        {
            encoderConfig.absoluteSensorRange = AbsoluteSensorRange.Signed_PlusMinus180;
            encoderConfig.magnetOffsetDegrees = encoderOffset;
            encoderConfig.sensorDirection = CANCODER_DIRECTION;
        }
        canCoder.configAllSettings(encoderConfig);
    }

    public void update(SwerveModuleState state) {
        turnMotor.config_kP(0, ShuffleBoard.wheelTurnKP.getDouble(WHEEL_TURN_KP));
        turnMotor.config_kI(1, ShuffleBoard.wheelTurnKI.getDouble(WHEEL_TURN_KI));
        turnMotor.config_kD(2, ShuffleBoard.wheelTurnKD.getDouble(WHEEL_TURN_KD));

        Rotation2d normalizedEncoder = Rotation2d.fromDegrees(Utils.normalizeAngleDegrees(canCoder.getPosition()));

        // Optimize the state
        state = SwerveModuleState.optimize(state, normalizedEncoder);
    
        // Do some angle weirdness to emulate a continuous PID controller and turn
        double weird;
        turnMotor.setSelectedSensorPosition(weird = 1000 * Utils.normalizeAngleDegrees(normalizedEncoder.getDegrees() - state.angle.getDegrees()));
        turnMotor.set(ControlMode.Position, 0);

        // Drive
        double drive = Utils.clamp(state.speedMetersPerSecond / ROBOT_MAX_VELOCITY, -1, 1);
        driveMotor.set(ControlMode.PercentOutput, drive * ShuffleBoard.wheelDriveScale.getDouble(0));
    }

    public double getCanRotation() {
        return canCoder.getAbsolutePosition();
    }

    public void disable() {
        // Nothing to do
    }
}
