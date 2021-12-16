package frc.robot;

import frc.robot.util.Vec2d;

public final class DriveController {
    private final DriveTrain driveTrain;
    private final Input input;

    public DriveController(DriveTrain driveTrain, Input input) {
        this.driveTrain = driveTrain;
        this.input = input;
    }

    public Vec2d update() {
        double driveSpeed = 0.3;
        
        double turn = input.getTurn();
        double drive = input.getDrive();

        double leftDrive = (drive + turn) * driveSpeed;
        double rightDrive = (drive - turn) * driveSpeed;
        return new Vec2d(leftDrive, rightDrive);
    }
}
