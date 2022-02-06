package frc.messenger.client;

import java.io.DataInputStream;
import java.io.IOException;

@FunctionalInterface
public interface MessageHandlerFunc {
    void handleMessage(String type, DataInputStream in) throws IOException;
}
