package frc.shufflewood.tools.taskmanager;

public final class Messages {
    public static final String START_TASK = ":Start";
    public static final String STOP_TASK = ":Stop";
    public static final String DELETE_TASK = ":Delete";
    public static final String UPLOAD_TASK = ":Upload";
    public static final String GET_TASKS = ":GetTasks";
    public static final String IS_TASK_RUNNING = ":IsRunning";

    public static final String STDOUT = ":StdOut:";
    public static final String STDERR = ":StdErr:";
    public static final String TASKS_RESPONSE = ":Tasks";
    public static final String RUNNING_RESPONSE = ":Running";

    private Messages() {
        throw new AssertionError();
    }
}
