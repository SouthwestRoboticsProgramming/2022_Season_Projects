package frc.taskmanager;

public final class Main {
    public static void main(String[] args) {
        System.out.println("TaskManager starting");

        TaskManager manager = new TaskManager("localhost", 8341);
        manager.run();
    }

    private Main() {
        throw new AssertionError();
    }
}
