package frc.shufflewood.tools.messenger;

import frc.messenger.client.MessengerClient;
import frc.shufflewood.App;
import frc.shufflewood.gui.GuiContext;
import frc.shufflewood.gui.filter.RangedIntegerFilter;
import frc.shufflewood.gui.filter.TextFilter;
import frc.shufflewood.tools.taskmanager.TaskManagerTool;
import frc.shufflewood.tools.Tool;

public class MessengerConnectTool implements Tool {
    private final App app;
    private final StringBuffer host;
    private final int[] port;
    private final StringBuffer name;

    private final TextFilter portFilter;

    private String status = "Enter Messenger address";

    public MessengerConnectTool(App app) {
        this.app = app;
        host = new StringBuffer("localhost");
        port = new int[] { 8341 };
        name = new StringBuffer("ShuffleWood");

        portFilter = new RangedIntegerFilter(0, 65535);
    }

    @Override
    public void draw(GuiContext gui) {
        gui.begin("Connect to Messenger");
        gui.setWindowSize(300, 275);
        gui.setWindowCenterPos(app.width / 2f, app.height / 2f);

        gui.text(status);

        gui.separator();

        // gui.columns(1, 1);
        gui.text("Host:");
        gui.editString(host);
        gui.text("Port:");
        gui.editInt(port, port, portFilter);
        gui.text("Name:");
        gui.editString(name);

        gui.separator();

        //gui.columns(1);
        if (gui.button("Connect")) {
            try {
                MessengerClient client = new MessengerClient(host.toString(), port[0], name.toString());
                app.setMessenger(client);
                //app.openTool(new TaskManagerTool());
                app.closeTool(this);
            } catch (RuntimeException e) {
                e.printStackTrace();
                status = "Connection failed!";
            }
        }

        gui.end();
    }
}
