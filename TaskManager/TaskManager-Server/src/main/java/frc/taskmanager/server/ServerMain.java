package frc.taskmanager.server;

import java.io.File;

public class ServerMain {
    public static void main(String[] args) {
        System.out.println("Starting the task manager server...");
        new TaskManagerServer(new File("tasks"), 8263, 8264).run();
    }
}
