package frc.taskmanager;

import java.io.File;

public final class Main {
    public static void main(String[] args) {
        System.out.println("TaskManager starting");

        TaskManager manager = new TaskManager("localhost", 8341, new File("tasks"));
        manager.run();
    }

    private Main() {
        throw new AssertionError();
    }
}
