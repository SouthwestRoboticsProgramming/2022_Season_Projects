package frc.messenger.test;

import frc.messenger.client.MessengerClient;

import java.util.Scanner;

public class MessengerTest {
    public static void main(String[] args) throws Exception {
        MessengerClient client = new MessengerClient("10.21.29.17", 8341, "Tester");

        client.setCallback((type, data) -> {
            System.out.println("Got " + type);
        });

        Scanner in = new Scanner(System.in);

        while (true) {
            client.read();

            if (System.in.available() > 0) {
                String line = in.nextLine();
                if (line.length() == 0) continue;

                String[] tokens = line.split(" ");

                switch (tokens[0]) {
                    case "send":
                        client.sendMessage(tokens[1], new byte[0]);
                        System.out.println("Sent " + tokens[1]);
                        break;
                    case "listen":
                        client.listen(tokens[1]);
                        System.out.println("Listening to " + tokens[1]);
                        break;
                    case "unlisten":
                        client.unlisten(tokens[1]);
                        System.out.println("No longer listening to " + tokens[1]);
                        break;
                    default:
                        System.out.println("Unknown command");
                        break;
                }
            }

            Thread.sleep(25);
        }
    }
}
