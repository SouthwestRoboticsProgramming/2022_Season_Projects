package frc.robot.subsystems;

import frc.robot.Robot;
import frc.robot.Scheduler;

public abstract class Subsystem {
    public Subsystem() {
        Scheduler.get().addSubsystem(this);
    }

    protected void disabledInit() {}
    protected void autonomousInit() {}
    protected void teleopInit() {}
    protected void testInit() {}

    protected void robotPeriodic() {}
    protected void disabledPeriodic() {}
    protected void autonomousPeriodic() {}
    protected void teleopPeriodic() {}
    protected void testPeriodic() {}

    public void doPeriodic() {
        robotPeriodic();

        switch (Robot.get().getState()) {
            case DISABLED:
                disabledPeriodic();
                break;
            case AUTONOMOUS:
                autonomousPeriodic();
                break;
            case TELEOP:
                teleopPeriodic();
                break;
            case TEST:
                testPeriodic();
                break;
        }
    }

    public void doInit() {
        switch (Robot.get().getState()) {
            case DISABLED:
                disabledInit();
                break;
            case AUTONOMOUS:
                autonomousInit();
                break;
            case TELEOP:
                teleopInit();
                break;
            case TEST:
                testInit();
                break;
        }
    }
}
