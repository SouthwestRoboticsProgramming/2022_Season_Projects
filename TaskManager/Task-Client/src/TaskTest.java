import frc.taskmanager.taskclient.TaskMessenger;

public class TaskTest {
    public static void main(String[] args) throws Exception {
        TaskMessenger m = new TaskMessenger("localhost", 8264, "TestTask");

        m.setMessageCallback((type, data) -> {
            m.sendMessage("The message was: " + type, new byte[0]);
        });

        while (true) {
            m.read();
            Thread.sleep(10);
        }
    }
}
