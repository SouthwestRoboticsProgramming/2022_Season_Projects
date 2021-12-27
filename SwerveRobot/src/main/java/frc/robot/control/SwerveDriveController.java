package frc.robot.control;

import frc.robot.drive.SwerveDrive;

import static frc.robot.Constants.JOYSTICK_DEAD_ZONE;

public class SwerveDriveController {
    private final SwerveDrive drive;
    private final Input input;
    
    public SwerveDriveController(SwerveDrive drive, Input input) {
        this.drive = drive;
        this.input = input;
    }

    public void update() {
        double driveX = input.getDriveX();
        double driveY = input.getDriveY();

        // Don't do anything if the control is within the dead zone
        if (driveX < JOYSTICK_DEAD_ZONE && driveY < JOYSTICK_DEAD_ZONE) {
            // Make sure the wheels are not driving
            drive.driveWheels(0);
        }

        // Find the angle of the joystick
        double angle = Math.atan2(driveY, driveX);
        System.out.println(angle);

        // Turn the wheels towards the angle
        drive.setWheelTargetAngle(angle);

        if (drive.wheelsAtTargetAngle()) {
            // If at the angle, drive
            drive.driveWheels(Math.sqrt(driveX * driveX + driveY * driveY));
        } else {
            // Otherwise, don't try to drive if the wheels aren't ready
            drive.driveWheels(0);
        }
    }
}
