package frc.shufflewood;

import frc.messenger.client.MessengerClient;
import frc.shufflewood.draw.Font;
import frc.shufflewood.gui.GuiContext;
import frc.shufflewood.tools.messenger.MessengerConnectTool;
import frc.shufflewood.tools.Tool;
import processing.core.PApplet;

import java.util.ArrayList;
import java.util.List;

public final class App extends PApplet {
    private GuiContext gui;
    private List<Tool> tools;
    private MessengerClient msg;

    public void setMessenger(MessengerClient msg) {
        this.msg = msg;
    }

    public void openTool(Tool tool) {
        tools.add(tool);
    }

    public void closeTool(Tool tool) {
        tools.remove(tool);
    }
    
    @Override
    public void settings() {
        size(800, 600, P2D);
        pixelDensity(displayDensity());
    }
    
    @Override
    public void setup() {
        surface.setResizable(true);        
        
        gui = new GuiContext(this);
        gui.getStyle().font = new Font(this, 20f/64f);

        tools = new ArrayList<>();
        tools.add(new MessengerConnectTool(this));
    }

    String lastKey = "";
    @Override
    public void draw() {
        if (msg != null)
            msg.read();

        background(64);

        gui.beginFrame();

        for (Tool tool : tools) {
            tool.draw(gui);
        }

        gui.begin("Keyboard");
        gui.text("Last key typed: " + lastKey);
        gui.end();

        gui.endFrame();
        
        gui.getDrawList().draw();
    }

    @Override
    public void keyTyped() {
        lastKey = String.valueOf(key);
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
    
    public static void main(String[] args) {
        PApplet.main(App.class.getName());
    }
}
