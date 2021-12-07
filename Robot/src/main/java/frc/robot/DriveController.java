package frc.robot;

public final class DriveController {
    private final DriveTrain driveTrain;
    private final Input input;

    public DriveController(DriveTrain driveTrain, Input input) {
        this.driveTrain = driveTrain;
        this.input = input;
    }

    public void update() {
        double driveSpeed = 0.3;
        
        double turn = input.getTurn();
        double drive = input.getDrive();

        double leftDrive = (drive + turn) * driveSpeed;
        double rightDrive = (drive - turn) * driveSpeed;
        driveTrain.driveMotors(leftDrive, rightDrive);
    }
}
