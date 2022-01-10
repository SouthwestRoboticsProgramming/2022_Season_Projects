package frc.shufflewood;

import frc.shufflewood.tools.LidarTool;
import processing.core.PApplet;

public final class App extends PApplet {
    private GuiContext gui;
    private LidarTool lidar;
    
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
        
        textSize(20);

        lidar = new LidarTool(this);
    }
    
    @Override
    public void draw() {
        background(64);

        gui.beginFrame();
        gui.begin("Test");
        gui.text("FPS: " + nf(frameRate, 0, 3));
        gui.separator();
        gui.text("Text content");
        gui.separator();
        gui.text("More content");
        if (gui.button("Button")) {
            gui.text("Click");
        }
        gui.separator();
        if (!gui.button("Recreate tree")) {
            if (gui.treePush("Tree")) {
                gui.treePop();
            }
        }
        gui.end();

        gui.begin("Test window 2");
        gui.text("Another window");
        gui.button("button 2");
        gui.end();

        lidar.draw(gui);
        gui.endFrame();
        
        gui.getDrawList().draw();
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
