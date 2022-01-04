package frc.shuffleplank.gui;

import frc.shuffleplank.gui.event.Event;
import frc.shuffleplank.gui.event.MouseMovedEvent;
import frc.shuffleplank.gui.event.MousePressedEvent;
import frc.shuffleplank.gui.event.MouseReleasedEvent;

import java.util.ArrayDeque;
import java.util.Queue;

public class GuiInput {
    private final Queue<Event> eventQueue;
    public float mouseX;
    public float mouseY;
    public float lastMouseX;
    public float lastMouseY;
    public boolean mouseDown = false;
    public boolean mouseClicked = false;

    public GuiInput() {
        eventQueue = new ArrayDeque<Event>();
    }

    public void update() {
        mouseClicked = false;
        lastMouseX = mouseX;
        lastMouseY = mouseY;

        Event event;
        while ((event = eventQueue.poll()) != null) {
            if (event instanceof MouseMovedEvent) {
                MouseMovedEvent e = (MouseMovedEvent) event;
                mouseX = e.x;
                mouseY = e.y;
            } else if (event instanceof MousePressedEvent) {
                mouseClicked = true;
                mouseDown = true;
            } else if (event instanceof MouseReleasedEvent) {
                mouseDown = false;
            }
        }
    }

    public void onMouseMoved(float x, float y) {
        MouseMovedEvent e = new MouseMovedEvent();
        e.x = x;
        e.y = y;
        eventQueue.add(e);
    }

    public void onMousePressed(float x, float y) {
        MousePressedEvent e = new MousePressedEvent();
        e.x = x;
        e.y = y;
        eventQueue.add(e);
    }

    public void onMouseReleased(float x, float y) {
        MouseReleasedEvent e = new MouseReleasedEvent();
        e.x = x;
        e.y = y;
        eventQueue.add(e);
    }

    public boolean mouseInRect(float x, float y, float w, float h) {
        return (lastMouseX > x && lastMouseY > y && lastMouseX < x + w && lastMouseY < y + h);
    }

    public Vec2f getDragInRect(float x, float y, float w, float h) {
        if (!mouseDown || lastMouseX < x || lastMouseX > x + w || lastMouseY < y || lastMouseY > y + h) {
            return new Vec2f(0, 0);
        }

        return new Vec2f(mouseX - lastMouseX, mouseY - lastMouseY);
    }
}
