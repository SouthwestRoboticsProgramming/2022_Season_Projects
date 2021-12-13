package frc.taskmanager;

import java.io.File;

public final class Main {
    public static void main(String[] args) {
        TaskManagerConfiguration config = TaskManagerConfiguration.loadFromFile(new File("config.properties"));

        TaskManager manager = new TaskManager(
            config.getMessengerHost(),
            config.getMessengerPort(),
            config.getTaskFolder(),
            config.getMessagePrefix()
        );
        manager.run();
    }

    private Main() {
        throw new AssertionError();
    }
}
