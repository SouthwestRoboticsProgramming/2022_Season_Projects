package frc.shufflewood.tools.messenger;

import frc.messenger.client.MessengerClient;
import frc.shufflewood.App;
import frc.shufflewood.gui.GuiContext;
import frc.shufflewood.gui.filter.RangedIntegerFilter;
import frc.shufflewood.gui.filter.TextFilter;
import frc.shufflewood.tools.StartingStateMenu;
import frc.shufflewood.tools.Tool;
import frc.shufflewood.tools.ToolPalette;
import frc.shufflewood.tools.taskmanager.TaskManagerTool;
import processing.core.PApplet;

public class MessengerConnectTool implements Tool {
    private final App app;
    private final StringBuffer host;
    private final int[] port;
    private final StringBuffer name;

    private final TextFilter portFilter;

    private String status = "Enter Messenger address";

    public MessengerConnectTool(App app) {
        this.app = app;
        host = new StringBuffer("10.21.29.3");
        port = new int[] { 8341 };
        name = new StringBuffer("ShuffleWood");

        portFilter = new RangedIntegerFilter(0, 65535);
    }

    @Override
    public void draw(GuiContext gui) {
        gui.begin("Connect to Messenger");
        gui.setWindowSize(300, 200);
        gui.setWindowCenterPos(app.width / 2f, app.height / 2f);

        gui.text(status);

        gui.separator();

        gui.beginTable(false, 1, 4);
        gui.text("Host:"); gui.tableNextColumn(); gui.editString(host); gui.tableNextColumn();
        gui.text("Port:"); gui.tableNextColumn(); gui.editInt(port, port, portFilter); gui.tableNextColumn();
        gui.text("Name:"); gui.tableNextColumn(); gui.editString(name);
        gui.endTable();

        gui.separator();

        if (gui.button("Connect")) {
            try {
                MessengerClient client = new MessengerClient(host.toString(), port[0], name.toString());
                app.setMessenger(client);
                app.openTool(new ToolPalette(app));
                app.openTool(new StartingStateMenu());
                app.closeTool(this);
            } catch (RuntimeException e) {
                e.printStackTrace();
                status = "Connection failed!";
            }
        }

        gui.setWindowHeightAuto();
        gui.end();
    }
}
