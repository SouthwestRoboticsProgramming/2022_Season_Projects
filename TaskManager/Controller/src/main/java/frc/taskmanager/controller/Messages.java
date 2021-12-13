package frc.taskmanager.controller;

public final class Messages {
    public static final String START_TASK = "TaskManager:Start";
    public static final String STOP_TASK = "TaskManager:Stop";
    public static final String DELETE_TASK = "TaskManager:Delete";
    public static final String UPLOAD_TASK = "TaskManager:Upload";
    public static final String GET_TASKS = "TaskManager:GetTasks";
    public static final String IS_TASK_RUNNING = "TaskManager:IsRunning";

    public static final String STDOUT = "TaskManager:StdOut:";
    public static final String STDERR = "TaskManager:StdErr:";
    public static final String TASKS_RESPONSE = "TaskManager:Tasks";
    public static final String RUNNING_RESPONSE = "TaskManager:Running";

    private Messages() {
        throw new AssertionError();
    }
}
