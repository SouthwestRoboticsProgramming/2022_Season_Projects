package frc.shuffleplank;

import frc.shuffleplank.gui.GuiContext;
import processing.core.PApplet;
import processing.core.PFont;

import java.io.IOException;

public final class ShufflePlank extends PApplet {
    public static ShufflePlank instance;

    public static int makeColor(float r, float g, float b) {
        return instance.color(255 * r, 255 * g, 255 * b);
    }

    private GuiContext gui;

    @Override
    public void settings() {
        fullScreen(P2D);
        //size(800, 600, P2D);
        instance = this;
    }

    @Override
    public void setup() {
        gui = new GuiContext();

        try {
            PFont font = new PFont(getClass().getResourceAsStream("/PTSans-Regular-14.vlw"));
            textFont(font);
        } catch (IOException e) {
            System.err.println("Failed to load custom font. Default font will be used.");
        }
    }

    boolean[] checkbox = {false};

    @Override
    public void draw() {
        background(0, 0, 0);

        gui.beginFrame(width, height);
        {
            gui.begin("Example window");
            gui.text("This is text.");
            gui.text("This is also text.");
            gui.separator();
            gui.textWrap("This text is wrapped if it goes off the side of the screen.");
            gui.spacing();
            gui.textWrap("This text is wrapped on characters instead of words", false);
            gui.separator();
            if (gui.button("Button")) {
                gui.spacing();
                gui.text("The button is pressed");
            } else {
                gui.spacing();
                gui.text("The button is not pressed");
            }
            gui.separator();
            gui.text("A checkbox:");
            gui.checkbox(checkbox);
            gui.end();

            gui.begin("Performance");
            gui.text("FPS: " + nf(frameRate, 0, 3));
            gui.end();

            gui.begin("Info window");
            gui.textWrap("A window can be moved by dragging its title bar.");
            gui.spacing();
            gui.textWrap("A window can be resized by dragging the bottom right corner of the window.");
            gui.end();

            gui.showDebugWindow();
        }
        gui.endFrame();

        gui.getDrawList().draw(this);
    }

    @Override
    public void mouseMoved() {
        gui.getInput().onMouseMoved(mouseX, mouseY);
    }

    @Override
    public void mousePressed() {
        gui.getInput().onMousePressed(mouseX, mouseY);
    }

    @Override
    public void mouseReleased() {
        gui.getInput().onMouseReleased(mouseX, mouseY);
    }

    @Override
    public void mouseDragged() {
        mouseMoved();
    }

    public static void main(String[] args) {
        PApplet.main(ShufflePlank.class.getName());
    }
}
