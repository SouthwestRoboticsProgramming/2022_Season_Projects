package frc.robot.command;

public interface Command {
    // Returns whether the command is finished running
    boolean run();

    // Run when the command is done
    default void end() {}

    // Gets the number of periodic cycles per iteration of this command
    default int getInterval() {
        return 1;
    }
}
