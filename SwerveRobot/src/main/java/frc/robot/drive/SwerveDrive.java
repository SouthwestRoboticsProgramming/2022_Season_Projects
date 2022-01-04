package frc.robot.drive;

import static frc.robot.Constants.*;

import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.geometry.Rotation2d;

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
    private final AHRS navx;

    public SwerveDrive() {
        w1 = new SwerveModule(DRIVE_PORT_1, TURN_PORT_1, CAN_PORT_1);
        w2 = new SwerveModule(DRIVE_PORT_2, TURN_PORT_2, CAN_PORT_2);
        w3 = new SwerveModule(DRIVE_PORT_3, TURN_PORT_3, CAN_PORT_3);
        w4 = new SwerveModule(DRIVE_PORT_4, TURN_PORT_4, CAN_PORT_4);
        navx = new AHRS(SPI.Port.kMXP, (byte) 200);
    }

    public void zeroGyroscope() {
        navx.zeroYaw();
    }

    public Rotation2d getGyroscopeRotation() {
        if (navx.isMagnetometerCalibrated()) {
            return Rotation2d.fromDegrees(navx.getFusedHeading());
        }
        return Rotation2d.fromDegrees(360.0 - navx.getYaw());
    }

    public void setWheelTargetAngle(double angle) {
        w1.setTargetAngle(angle);
        w2.setTargetAngle(angle);
        w3.setTargetAngle(angle);
        w4.setTargetAngle(angle);
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
        w1.update();
        w2.update();
        w3.update();
        w4.update();
    }
}
