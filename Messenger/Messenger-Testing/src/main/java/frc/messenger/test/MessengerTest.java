package frc.messenger.test;

import frc.messenger.client.MessengerClient;

import java.io.*;
import java.util.Scanner;

public class MessengerTest {
    public static void main(String[] args) throws Exception {
        MessengerClient client = new MessengerClient("10.21.29.3", 8341, "Tester");

        client.setCallback((type, data) -> {
            //System.out.println("Got " + type);
            DataInputStream in = new DataInputStream(new ByteArrayInputStream(data));
            try {
                boolean good = in.readBoolean();
                if (good) {
                    double xAngle = in.readDouble();
                    double xAngle2 = in.readDouble();
                    double yAngle = in.readDouble();

                    System.out.println(xAngle + " " + xAngle2 + " " + yAngle);
                } else {
                    System.out.println("bad");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        Scanner in = new Scanner(System.in);
        System.out.println("Ready.");

        while (true) {
            client.read();

            if (System.in.available() > 0) {
                String line = in.nextLine();
                if (line.length() == 0) continue;

                String[] tokens = line.split(" ");

                switch (tokens[0]) {
                    case "send": {
                        byte[] data;
                        if (tokens.length > 2) {
                            StringBuilder builder = new StringBuilder();
                            boolean space = false;
                            for (int i = 2; i < tokens.length; i++) {
                                if (space) {
                                    builder.append(" ");
                                }
                                space = true;
                                builder.append(tokens[i]);
                            }

                            ByteArrayOutputStream b = new ByteArrayOutputStream();
                            DataOutputStream d = new DataOutputStream(b);
                            try {
                                d.writeUTF(builder.toString());
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
                    default:
                        System.out.println("Unknown command");
                        break;
                }
            }

            Thread.sleep(25);
        }
    }
}
