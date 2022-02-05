package frc.messenger.test;

import frc.messenger.client.MessengerClient;

import java.io.*;
import java.util.Scanner;

public class MessengerTest {
    public static void main(String[] args) throws Exception {
        MessengerClient client = new MessengerClient("localhost", 8341, "Tester");

        client.setCallback((type, data) -> {
            System.out.println("Got " + type);
        });

        Scanner in = new Scanner(System.in);
        System.out.println("Ready.");

        ShuffleWood.init(client);

        main: while (true) {
            client.read();

            if (System.in.available() > 0) {
                String line = in.nextLine();
                if (line.length() == 0) continue;

                String[] tokens = line.split(" ");

                switch (tokens[0]) {
                    case "send": {
                        byte[] data;
                        if (tokens.length > 2) {
                            ByteArrayOutputStream b = new ByteArrayOutputStream();
                            DataOutputStream d = new DataOutputStream(b);
                            try {
                                for (int i = 2; i < tokens.length; i++) {
                                    d.writeUTF(tokens[i]);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            data = b.toByteArray();
                        } else {
                            data = new byte[0];
                        }

                        client.sendMessage(tokens[1], data);
                        System.out.println("Sent " + tokens[1]);
                        break;
                    }
                    case "listen":
                        client.listen(tokens[1]);
                        System.out.println("Listening to " + tokens[1]);
                        break;
                    case "unlisten":
                        client.unlisten(tokens[1]);
                        System.out.println("No longer listening to " + tokens[1]);
                        break;
                    case "set": {
                        String key = tokens[1];

                        if (tokens[2].equals("int")) {
                            ShuffleWood.setInt(key, Integer.parseInt(tokens[3]));
                        } else if (tokens[2].equals("double")){
                            ShuffleWood.setDouble(key, Double.parseDouble(tokens[3]));
                        }

                        break;
                    }
                    case "debug":
                        ShuffleWood.debug();
                        break;
                    case "stop":
                        break main;
                    default:
                        System.out.println("Unknown command");
                        break;
                }
            }

            Thread.sleep(25);
        }

        ShuffleWood.save();
    }
}
