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
                    double[] d = new double[5];
                    for (int i = 0; i < d.length; i++) {
                        d[i] = in.readDouble();
                    }

                    for (double v : d) {
                        System.out.print(v + " ");
                    }
                    System.out.println();
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
                    default:
                        System.out.println("Unknown command");
                        break;
                }
            }

            Thread.sleep(25);
        }
    }
}
