package frc.messenger.test;

import frc.messenger.client.MessengerClient;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class ShuffleWood {
    private static final String SET_VALUE = "ShuffleWood:SetValue";
    private static final String LOAD_VALUE = "ShuffleWood:LoadValue";
    private static final String UPDATE_VALUE = "ShuffleWood:UpdateValue";
    private static final String POLL_VALUES = "ShuffleWood:PollValues";

    private static MessengerClient msg;
    private static File storageFile;
    private static Map<String, Number> values;

    public static void init(MessengerClient msg) {
        ShuffleWood.msg = msg;
        msg.listen(POLL_VALUES);
        msg.listen(UPDATE_VALUE);

        values = new HashMap<>();

        storageFile = new File("shuffledata.txt");
        try {
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
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        try {
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(b);

            out.writeUTF(key);
            out.writeByte(1);
            out.writeInt(value);

            msg.sendMessage(LOAD_VALUE, b.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void setDouble(String key, double value) {
        try {
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(b);

            out.writeUTF(key);
            out.writeByte(2);
            out.writeDouble(value);

            msg.sendMessage(LOAD_VALUE, b.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void debug() {
        return;
    }
}
