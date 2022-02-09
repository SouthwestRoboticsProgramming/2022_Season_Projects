package frc.shufflewood.tools;

import frc.shufflewood.MessengerAccess;
import frc.shufflewood.gui.GuiContext;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class ValueEditTool implements Tool {
    private static final String POLL_MESSAGE = "ShuffleWood:PollValues";
    private static final String LOAD_MESSAGE = "ShuffleWood:LoadValue";
    private static final String UPDATE_MESSAGE = "ShuffleWood:UpdateValue";

    private final MessengerAccess msg;
    private final Map<String, Value> values;

    private abstract static class Value {
        protected final String key;

        public Value(String key) {
            this.key = key;
        }

        public abstract void read(DataInputStream in) throws IOException;
        public abstract void edit(GuiContext gui);
    }

    private final class IntValue extends Value {
        public static final int TYPE_ID = 1;

        private int[] value;

        public IntValue(String key) {
            super(key);
        }

        @Override
        public void read(DataInputStream in) throws IOException {
            value = new int[] {in.readInt()};
        }

        @Override
        public void edit(GuiContext gui) {
            if (gui.editInt(value)) {
                ByteArrayOutputStream b = new ByteArrayOutputStream();
                DataOutputStream out = new DataOutputStream(b);

                try {
                    out.writeUTF(key);
                    out.writeByte(TYPE_ID);
                    out.writeInt(value[0]);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                msg.sendMessage(UPDATE_MESSAGE, b.toByteArray());
            }
        }
    }

    private final class DoubleValue extends Value {
        public static final int TYPE_ID = 2;

        private double[] value;

        public DoubleValue(String key) {
            super(key);
        }

        @Override
        public void read(DataInputStream in) throws IOException {
            value = new double[] {in.readDouble()};
        }

        @Override
        public void edit(GuiContext gui) {
            if (gui.editDouble(value)) {
                ByteArrayOutputStream b = new ByteArrayOutputStream();
                DataOutputStream out = new DataOutputStream(b);

                try {
                    out.writeUTF(key);
                    out.writeByte(TYPE_ID);
                    out.writeDouble(value[0]);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                msg.sendMessage(UPDATE_MESSAGE, b.toByteArray());
            }
        }
    }

    public ValueEditTool(MessengerAccess msg) {
        this.msg = msg;
        values = new HashMap<>();

        msg.listen(LOAD_MESSAGE, this::onMessage);
        msg.sendMessage(POLL_MESSAGE, new byte[0]);
    }

    private void onMessage(String type, byte[] data) {
        ByteArrayInputStream b = new ByteArrayInputStream(data);
        DataInputStream in = new DataInputStream(b);

        try {
            if (type.equals(LOAD_MESSAGE)) {
                String key = in.readUTF();
                byte dataType = in.readByte();
                Value value;
                if (dataType == IntValue.TYPE_ID) { // Data is int
                    value = new IntValue(key);
                } else if (dataType == DoubleValue.TYPE_ID) { // Data is double
                    value = new DoubleValue(key);
                } else return;

                value.read(in);

                values.put(key, value);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(type);
    }

    @Override
    public void draw(GuiContext gui) {
        gui.begin("Value Edit Panel");

        if (gui.button("Refresh")) {
            msg.sendMessage(POLL_MESSAGE, new byte[0]);
        }

        gui.beginTable(false, false, 1, 3);
        boolean first = true;
        for (Map.Entry<String, Value> entry : values.entrySet()) {
            if (!first) {
                gui.tableNextColumn();
            } else {
                first = false;
            }

            gui.text(entry.getKey());
            gui.tableNextColumn();

            entry.getValue().edit(gui);
        }
        gui.endTable();

        gui.end();
    }
}
