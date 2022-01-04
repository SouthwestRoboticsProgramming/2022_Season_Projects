package frc.robot.drive;

import static frc.robot.Constants.*;
import edu.wpi.first.wpilibj.kinematics.chassisSpeeds;
import edu.wpi.first.wpilibj.kinematics.SwerveDriveKinematics;
import edu.wpi.first.wpilibj.kinematics.SwereveModuleStates;

public class SwerveDrive {
    /*
     * Wheel Layout:
     * 
     * w1 ------- w2
     *  |         |
     *  |  ---->  |
     *  |         |
     * w4 ------- w3
     */
    private final SwerveModule w1, w2, w3, w4;
    private final ChassisSpeeds chassisSpeeds;

    public SwerveDrive() {
        w1 = new SwerveModule(DRIVE_PORT_1, TURN_PORT_1, CAN_PORT_1);
        w2 = new SwerveModule(DRIVE_PORT_2, TURN_PORT_2, CAN_PORT_2);
        w3 = new SwerveModule(DRIVE_PORT_3, TURN_PORT_3, CAN_PORT_3);
        w4 = new SwerveModule(DRIVE_PORT_4, TURN_PORT_4, CAN_PORT_4);
        chassisSpeeds = new ChassisSpeeds(0.0, 0.0, 0.0);
    }

    public void setWheelTargetAngle(double angle) {
        w1.setTargetAngle(angle + STARTING_POS_1);
        w2.setTargetAngle(angle + STARTING_POS_2);
        w3.setTargetAngle(angle + STARTING_POS_3);
        w4.setTargetAngle(angle + STARTING_POS_4);
    }

    public boolean wheelsAtTargetAngle() {
        return w1.isAtTargetAngle()
            && w2.isAtTargetAngle()
            && w3.isAtTargetAngle()
            && w4.isAtTargetAngle();
    }

    public void driveWheels(double amount) {
        w1.drive(amount);
        w2.drive(amount);
        w3.drive(amount);
        w4.drive(amount);
    }

    public void update() {
        SwereveModuleStates[] states = kinematics.toSwereveModuleStates(chassisSpeeds);
        w2.set(states[0].speedMetersPerSecond / MAX_VELOCITY * MAX_VOLTAGE, states[0].angle.getRadians());
        w3.set(states[1].speedMetersPerSecond / MAX_VELOCITY * MAX_VOLTAGE, states[1].angle.getRadians());
        w4.set(states[2].speedMetersPerSecond / MAX_VELOCITY * MAX_VOLTAGE, states[2].angle.getRadians());
        w1.set(states[3].speedMetersPerSecond / MAX_VELOCITY * MAX_VOLTAGE, states[3].angle.getRadians());



        // w1.update();
        // w2.update();
        // w3.update();
        // w4.update();

    }

    public void disable() {
        w1.disable();
        w2.disable();
        w3.disable();
        w4.disable();
    }
}
