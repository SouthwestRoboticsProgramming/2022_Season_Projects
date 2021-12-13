package frc.taskmanager.controller;

public class ControllerMain {
    public static void main(String[] args) {
        if (args.length > 0) {
            new TaskManagerController().run(args[0], Integer.parseInt(args[1]), args[2]);
        } else {
            new TaskManagerController().run();
        }
    }
}
