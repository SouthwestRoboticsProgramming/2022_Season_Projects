package frc.taskmanager;

public final class Messages {
    public static final String START_TASK = "TaskManager:Start";
    public static final String STOP_TASK = "TaskManager:Stop";
    public static final String DELETE_TASK = "TaskManager:Delete";
    public static final String UPLOAD_TASK = "TaskManager:Upload";

    public static final String STDOUT = "TaskManager:StdOut:";
    public static final String STDERR = "TaskManager:StdErr:";

    private Messages() {
        throw new AssertionError();
    }
}
