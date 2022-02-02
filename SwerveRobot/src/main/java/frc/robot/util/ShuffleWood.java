package frc.robot.util;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import frc.messenger.client.MessengerClient;

public final class ShuffleWood {
    private static MessengerClient msg;

    public static void setMessenger(MessengerClient msg) {
        ShuffleWood.msg = msg;
    }

    public static void set(String key, Object value) {
        try {
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(b);
            
            out.writeUTF(key);
            out.writeUTF(value.toString());

            msg.sendMessage("ShuffleWood:SetValue", b.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}