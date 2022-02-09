package frc.robot.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import edu.wpi.first.wpilibj.Filesystem;
import frc.messenger.client.MessageDispatcher;
import frc.messenger.client.MessageHandler;

public final class ShuffleWood {
    private static final String SET_VALUE = "ShuffleWood:SetValue";
    private static final String LOAD_VALUE = "ShuffleWood:LoadValue";
    private static final String UPDATE_VALUE = "ShuffleWood:UpdateValue";
    private static final String POLL_VALUES = "ShuffleWood:PollValues";

    private static MessageDispatcher msg;
    private static File storageFile;
    private static Map<String, Number> values;

    public static void setMessenger(MessageDispatcher msg) {
        ShuffleWood.msg = msg;
        msg.addMessageHandler(
            new MessageHandler()
                .setHandler(ShuffleWood::onMessage)
                .listen(UPDATE_VALUE)
                .listen(POLL_VALUES)
        );

        storageFile = new File(Filesystem.getOperatingDirectory(), "shufflewood-data.txt");
        values = new HashMap<>();

        try {
            load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void load() throws IOException {
        if (!storageFile.exists()) storageFile.createNewFile();

        BufferedReader br = new BufferedReader(new FileReader(storageFile));

        String line;
        while ((line = br.readLine()) != null) {
            String[] tokens = line.split(",");

            String name = tokens[0];
            String type = tokens[1];
            String value = tokens[2];

            if (type.equals("int")) {
                values.put(name, Integer.parseInt(value));
            } else if (type.equals("double")) {
                values.put(name, Double.parseDouble(value));
            }
        }

        br.close();
    }

    public static void save() {
        try {
            PrintWriter out = new PrintWriter(new FileWriter(storageFile));

            for (Map.Entry<String, Number> entry : values.entrySet()) {
                String name = entry.getKey();
                String type = entry.getValue() instanceof Integer ? "int" : "double";
                String value = entry.getValue().toString();

                out.println(name + "," + type + "," + value);
            }

            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void updateValue(DataInputStream in) throws IOException {
        String name = in.readUTF();
        System.out.println("Setting " + name);
        byte type = in.readByte();
        Number value;
        if (type == 1) {
            value = in.readInt();
        } else if (type == 2) {
            value = in.readDouble();
        } else return;

        values.put(name, value);
    }

    private static void sendLoadValue(String name, Number value) throws IOException {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);

        out.writeUTF(name);
        if (value instanceof Integer) {
            out.writeByte(1);
            out.writeInt(value.intValue());
        } else {
            out.writeByte(2);
            out.writeDouble(value.doubleValue());
        }

        msg.sendMessage(LOAD_VALUE, b.toByteArray());
        System.out.println("Send!");
    }

    private static void pollValues(DataInputStream in) throws IOException {
        for (Map.Entry<String, Number> entry : values.entrySet()) {
            sendLoadValue(entry.getKey(), entry.getValue());
        }
    }

    private static void onMessage(String type, DataInputStream in) throws IOException {
        switch (type) {
            case UPDATE_VALUE: updateValue(in); break;
            case POLL_VALUES: pollValues(in); break;
        }
    }

    public static void show(String key, Object value) {
        try {
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(b);

            out.writeUTF(key);
            out.writeUTF(value.toString());

            msg.sendMessage(SET_VALUE, b.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void setInt(String key, int value) {
        values.put(key, value);
        try {
            sendLoadValue(key, value);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void setDouble(String key, double value) {
        values.put(key, value);
        try {
            sendLoadValue(key, value);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int getInt(String key, int default_) {
        if (!values.containsKey(key)) {
            setInt(key, default_);
        }

        return values.get(key).intValue();
    }

    public static double getDouble(String key, double default_) {
        if (!values.containsKey(key)) {
            setDouble(key, default_);
        }

        return values.get(key).doubleValue();
    }
}