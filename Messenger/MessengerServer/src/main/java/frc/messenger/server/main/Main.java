package frc.messenger.server.main;

import frc.messenger.server.MessengerServer;

public final class Main {
    public static void main(final String[] args) {
        System.out.println("Messenger server starting");
        new MessengerServer().run();
    }

    private Main() {
        throw new AssertionError();
    }
}
