package frc.shufflewood.tools;

import frc.shufflewood.MessengerAccess;
import frc.shufflewood.gui.GuiContext;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ValueDisplayTool implements Tool {
    private final MessengerAccess msg;
    private final Map<String, String> values;

    public ValueDisplayTool(MessengerAccess msg) {
        this.msg = msg;
        values = new HashMap<>();

        msg.listen("ShuffleWood:SetValue", this::onMessage);
    }

    private void onMessage(String type, byte[] data) {
        try {
            DataInputStream in = new DataInputStream(new ByteArrayInputStream(data));
            String key = in.readUTF();
            String value = in.readUTF();
            values.put(key, value);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void draw(GuiContext gui) {
        gui.begin("Value Display");

        gui.beginTable(true, 1, 1);
        boolean first = true;
        for (Map.Entry<String, String> entry : values.entrySet()) {
            if (!first) {
                gui.tableNextColumn();
            } else {
                first = false;
            }

            gui.text(entry.getKey());
            gui.tableNextColumn();
            gui.text(entry.getValue());
        }
        gui.endTable();

        gui.end();
    }
}
