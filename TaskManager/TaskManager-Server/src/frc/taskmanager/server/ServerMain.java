package frc.taskmanager.server;

import java.io.File;

public class ServerMain {
    public static void main(String[] args) {
        new TaskManagerServer(new File("tasks"), 8263, 8264).run();
    }
}
