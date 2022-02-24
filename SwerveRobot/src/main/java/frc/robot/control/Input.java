package frc.robot.control;

import static frc.robot.constants.ControlConstants.*;

public class Input {
    private final XboxController drive;
    //private final XboxController manipulator;

    private final XboxController finalManipulator;
    private final XboxController finalDrive;

    public Input() {
        drive = new XboxController(DRIVE_CONTROLLER);
        //manipulator = new XboxController(11);

        finalManipulator = new XboxController(MANIPULATOR_CONTROLLER);
        finalDrive = new XboxController(DRIVE_CONTROLLER);
    }

    public double getDriveX() {
        return drive.getLeftStickX();
    }

    public double getDriveY() {
        return drive.getLeftStickY();
    }

    public double getRot() {
        return -drive.getRightStickX();
    }






    /* Final Controler Setups */
    public boolean getIntake() {
        return drive.getLeftShoulderButton();
    }

    private boolean intakePreviousButton = false;
    private boolean intakePrevious = false;

    public boolean getIntakeLift() {
        boolean finalIntake = false;

        /* Get leading edge */
        boolean pressed = drive.getYButton() && drive.getYButton() != intakePreviousButton;

        /* If it's pressed, toggle the intake */
        if (pressed) {
            finalIntake = !intakePrevious;
        } else {
            finalIntake = intakePrevious;
        }
        
        intakePrevious = finalIntake;
        intakePreviousButton = drive.getYButton();

        return finalIntake;
    }

    private boolean shootPrevious = false;
    public boolean getShoot() {
        if (drive.getAButton() && drive.getAButton() != shootPrevious) {
            shootPrevious = drive.getAButton();
            return true;
        } else {
            shootPrevious = drive.getAButton();
            return false; 
        }
    }

    public boolean getAim() {
        return finalDrive.getRightShoulderButton() || drive.getRightShoulderButton();
    }
    /* Climber */
    public double getClimbTele() {
        return finalManipulator.getLeftStickY();
    }

    public boolean getClimbNextStep() {
        return finalManipulator.getLeftShoulderButton() && finalManipulator.getRightShoulderButton();
    }
}
