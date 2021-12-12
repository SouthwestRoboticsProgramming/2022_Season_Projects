package frc.messenger.test;

import frc.messenger.client.MessengerClient;

public class MessengerTest {
    public static void main(String[] args) throws Exception {
        MessengerClient client = new MessengerClient("localhost", 8341, "Tester");

        client.setCallback((type, data) -> {
            System.out.println("Got " + type + " with data:");
            for (byte b : data) {
                System.out.println(b);
            }
        });
        client.listen("Test");

        while (true) {
            client.sendMessage("Test", new byte[] {1, 2, 3, 4, 5});
            client.read();

            Thread.sleep(25);
        }
    }
}
