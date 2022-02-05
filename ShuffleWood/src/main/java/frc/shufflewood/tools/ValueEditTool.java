package frc.shufflewood.tools;

import frc.shufflewood.MessengerAccess;
import frc.shufflewood.gui.GuiContext;

public class ValueEditTool implements Tool {
    private final MessengerAccess msg;

    public ValueEditTool(MessengerAccess msg) {
        this.msg = msg;
    }

    private void onMessage(String type, byte[] data) {
        
    }

    @Override
    public void draw(GuiContext gui) {

    }
}
