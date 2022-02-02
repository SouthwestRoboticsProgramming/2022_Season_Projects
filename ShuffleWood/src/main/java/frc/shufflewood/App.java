package frc.shufflewood;

import frc.messenger.client.MessengerClient;
import frc.shufflewood.gui.GuiContext;
import frc.shufflewood.gui.GuiContextImpl;
import frc.shufflewood.gui.draw.Font;
import frc.shufflewood.gui.draw.GuiDrawData;
import frc.shufflewood.tools.Tool;
import frc.shufflewood.tools.messenger.MessengerConnectTool;
import processing.core.PApplet;
import processing.core.PImage;
import processing.event.MouseEvent;

import java.util.ArrayList;
import java.util.List;

public final class App extends PApplet {
    @Override
    public void settings() {
        size(1280, 720, P2D);
        if (System.getProperty("os.name").toLowerCase().contains("mac")) {
            throw new RuntimeException("ShuffleWood does not support MacOS, as some important features do not work.");
        }
    }

    private GuiContext gui;
    private List<Tool> tools, openedTools, closedTools;
    private MessengerAccess msg;

    public void setMessenger(MessengerClient msg) {
        this.msg = new MessengerAccess(msg);
    }

    public MessengerAccess getMessenger() {
        return msg;
    }

    public void openTool(Tool tool) {
        openedTools.add(tool);
    }

    public void closeTool(Tool tool) {
        closedTools.add(tool);
    }

    @Override
    public void setup() {
        surface.setResizable(true);

        gui = new GuiContextImpl(this);
        gui.getStyle().font = new Font(this, 0.25f);

        tools = new ArrayList<>();
        openedTools = new ArrayList<>();
        closedTools = new ArrayList<>();
        tools.add(new MessengerConnectTool(this));
    }

    @Override
    public void draw() {
        if (msg != null)
            msg.read();

        tools.addAll(openedTools);
        tools.removeAll(closedTools);
        openedTools.clear();
        closedTools.clear();

        background(42);

        gui.beginFrame(width, height);

        for (Tool tool : tools) {
            tool.draw(gui);
        }

        gui.endFrame();

        PImage lastTexture = null;
        noStroke();
        beginShape(TRIANGLES);
        GuiDrawData data = gui.getDrawData();
        for (GuiDrawData.Vertex v : data.getVertices()) {
            if (lastTexture != v.getTexture()) {
                lastTexture = v.getTexture();
                endShape();
                beginShape(TRIANGLES);
                texture(v.getTexture());
            }
            tint(v.tint);
            vertex(v.pos.x, v.pos.y, v.uv.x, v.uv.y);
        }
        endShape();
    }

    @Override
    public void keyTyped() {
        gui.getInput().onCharTyped(key);
    }

    @Override
    public void mouseDragged() { mouseMoved(); }

    @Override
    public void mouseMoved() {
        gui.getInput().onMouseMoved(mouseX, mouseY);
    }

    @Override
    public void mousePressed() {
        gui.getInput().onMouseDown(mouseX, mouseY);
    }

    @Override
    public void mouseReleased() {
        gui.getInput().onMouseUp(mouseX, mouseY);
    }

    @Override
    public void mouseWheel(MouseEvent event) {
        gui.getInput().onMouseScrolled(mouseX, mouseY, 0, event.getCount());
    }

    public static void main(String[] args) {
        PApplet.main(App.class.getName());
    }
}
