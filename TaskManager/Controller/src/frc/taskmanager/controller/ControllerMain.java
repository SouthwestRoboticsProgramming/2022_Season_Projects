package frc.taskmanager.controller;

public class ControllerMain {
    public static void main(String[] args) {
        if (args.length > 0) {
            new CoprocessorController().run(args[0], Integer.parseInt(args[1]));
        } else {
            new CoprocessorController().run();
        }
    }
}
