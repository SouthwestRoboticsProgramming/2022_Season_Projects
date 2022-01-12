package frc.shufflewood.gui;

import frc.shufflewood.Vec2f;

import java.util.ArrayDeque;
import java.util.Queue;

public final class GuiInput {
  private static final int EVENT_MOUSE_MOVED = 0;
  private static final int EVENT_MOUSE_DOWN  = 1;
  private static final int EVENT_MOUSE_UP    = 2;

  private final Queue<Event> eventQueue;

  public float cursorX;
  public float cursorY;
  public boolean mouseDown;
  public boolean mouseClicked;
  private Queue<Character> charQueue;

  private float lastX;
  private float lastY;

  public GuiInput() {
    eventQueue = new ArrayDeque<>();

    cursorX = 0;
    cursorY = 0;
    mouseDown = false;
    mouseClicked = false;
    charQueue = new ArrayDeque<>();

    lastX = 0;
    lastY = 0;
  }

  public void update() {
    lastX = cursorX;
    lastY = cursorY;
    mouseClicked = false;
    charQueue.clear();

    Event event;
    while ((event = eventQueue.poll()) != null) {
      if (event instanceof MouseEvent) {
        MouseEvent e = (MouseEvent) event;
        cursorX = e.x;
        cursorY = e.y;

        switch (e.type) {
          case EVENT_MOUSE_MOVED:
            break;
          case EVENT_MOUSE_DOWN:
            mouseDown = true;
            mouseClicked = true;
            break;
          case EVENT_MOUSE_UP:
            mouseDown = false;
            break;
        }
      } else if (event instanceof KeyEvent) {
        KeyEvent e = (KeyEvent) event;

        charQueue.add(e.c);
      }
    }
  }

  public String getTextInput() {
    StringBuilder b = new StringBuilder();
    Character c;
    while ((c = charQueue.poll()) != null) {
      b.append(c.charValue());
    }
    return b.toString();
  }

  public boolean rectHovered(float x, float y, float w, float h) {
    float cx = cursorX;
    float cy = cursorY;

    return cx > x && cx < x + w && cy > y && cy < y + h;
  }

  public boolean rectClicked(float x, float y, float w, float h) {
    return mouseClicked && rectHovered(x, y, w, h);
  }

  public boolean clickedOutsideRect(float x, float y, float w, float h) {
    return mouseClicked && !rectHovered(x, y, w, h);
  }

  public boolean rectPressed(float x, float y, float w, float h) {
    return mouseDown && rectHovered(x, y, w, h);
  }

  public Vec2f rectDrag(float x, float y, float w, float h) {
    float lx = lastX;
    float ly = lastY;

    if (!mouseDown || lx < x || lx > x + w || ly < y || ly > y + h) {
      return new Vec2f(0, 0);
    }

    return new Vec2f(cursorX - lx, cursorY - ly);
  }

  public void onCharTyped(char c) {
    eventQueue.add(new KeyEvent(c));
  }

  public void onMouseMoved(float x, float y) {
    eventQueue.add(new MouseEvent(
      EVENT_MOUSE_MOVED,
      x, y
    ));
  }

  public void onMouseDown(float x, float y) {
    eventQueue.add(new MouseEvent(
      EVENT_MOUSE_DOWN,
      x, y
    ));
  }

  public void onMouseUp(float x, float y) {
    eventQueue.add(new MouseEvent(
      EVENT_MOUSE_UP,
      x,y
    ));
  }

  private static abstract class Event {}

  private static class MouseEvent extends Event {
    private final int type;
    private final float x;
    private final float y;

    private MouseEvent(int type, float x, float y) {
      this.type = type;
      this.x = x;
      this.y = y;
    }
  }

  private static class KeyEvent extends Event {
    private final char c;

    public KeyEvent(char c) {
      this.c = c;
    }
  }
}