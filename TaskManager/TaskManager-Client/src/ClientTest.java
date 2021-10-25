import frc.taskmanager.client.Coprocessor;
import frc.taskmanager.client.Task;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ClientTest {
    public static void main(String[] args) throws Exception {
        Coprocessor cp = new Coprocessor("192.168.50.71", 8263);
        cp.connect();

        Task task = cp.getTask("TestTask2");

        Path path = Paths.get("ziptest.zip");
        task.upload(Files.readAllBytes(path));

        task.setMessageReceiveCallback((type, data) -> {
            System.out.println("Message from task: " + type);
        });
        task.start();

        task.sendMessage("Test Message!", new byte[0]);

        for (int i = 0; i < 500; i++) {
            cp.flushNetwork();
            Thread.sleep(10);
        }

        //task.stop();

        while (true) {
            cp.flushNetwork();
            Thread.sleep(10);
        }
    }
}
