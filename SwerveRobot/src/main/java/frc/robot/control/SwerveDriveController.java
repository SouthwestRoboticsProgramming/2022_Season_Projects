package frc.robot.control;

import frc.robot.drive.SwerveDrive;

import static frc.robot.Constants.JOYSTICK_DEAD_ZONE;

import frc.robot.Constants;

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


        double driveAmount = Math.sqrt(driveX * driveX + driveY * driveY);
        // Don't do anything if the control is within the dead zone
        if (Math.abs(driveAmount) < JOYSTICK_DEAD_ZONE) {
            // Make sure the wheels are not driving
            drive.driveWheels(0);
            // Set each of the wheels to starting value
            drive.setWheelTargetAngle(Constants.STARTING_WHEEL_ANGLE);
        }

        // Find the angle of the joystick
        double angle = Math.atan2(driveY, driveX);
        System.out.println(driveAmount);

        // Turn the wheels towards the angle
        drive.setWheelTargetAngle(angle);

        if (drive.wheelsAtTargetAngle()) {
            // If at the angle, check if the stick is in the deadzone
            if (Math.abs(driveAmount) > JOYSTICK_DEAD_ZONE) {
                drive.driveWheels(driveAmount);
            } else {
                drive.driveWheels(0);
            }
        } else {
            // Otherwise, don't try to drive if the wheels aren't ready
            drive.driveWheels(0);
        }

        drive.update();
    }
}
