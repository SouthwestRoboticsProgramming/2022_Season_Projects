package frc.shufflewood.gui.input;

import frc.shufflewood.gui.Rect;
import frc.shufflewood.gui.Vec2;

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
  private float scrollX;
  private float scrollY;

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
    scrollX = 0;
    scrollY = 0;
  }

  public void update() {
    lastX = cursorX;
    lastY = cursorY;
    scrollX = 0;
    scrollY = 0;
    mouseClicked = false;
    charQueue.clear();

    Event event;
    while ((event = eventQueue.poll()) != null) {
      EventDispatcher d = new EventDispatcher(event);

      d.dispatch(MouseEvent.class, (e) -> {
        cursorX = e.getX();
        cursorY = e.getY();
      });
      d.dispatch(MouseDownEvent.class, (e) -> {
        mouseDown = true;
        mouseClicked = true;
      });
      d.dispatch(MouseUpEvent.class, (e) -> {
        mouseDown = false;
      });
      d.dispatch(KeyTypedEvent.class, (e) -> {
        charQueue.add(e.getChar());
      });
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

  public Vec2 getScrollInRect(Rect r) {
    if (rectHovered(r)) {
      Vec2 scroll = new Vec2(scrollX, scrollY);
      scrollX = 0;
      scrollY = 0;
      return scroll;
    }
    return new Vec2();
  }

  public boolean rectHovered(Rect r) {
    float cx = cursorX;
    float cy = cursorY;

    return cx > r.min.x && cx < r.max.x && cy > r.min.y && cy < r.max.y;
  }

  public boolean rectClicked(Rect r) {
    return mouseClicked && rectHovered(r);
  }

  public boolean clickedOutsideRect(Rect r) {
    return mouseClicked && !rectHovered(r);
  }

  public boolean rectPressed(Rect r) {
    return mouseDown && rectHovered(r);
  }

  public Vec2 rectDrag(Rect r) {
    float lx = lastX;
    float ly = lastY;

    if (!mouseDown || lx < r.min.x || lx > r.max.x || ly < r.min.y || ly > r.max.y) {
      return new Vec2(0, 0);
    }

    return new Vec2(cursorX - lx, cursorY - ly);
  }

  public void onCharTyped(char c) {
    eventQueue.add(new KeyTypedEvent(c));
  }

  public void onMouseMoved(float x, float y) {
    eventQueue.add(new MouseMovedEvent(x, y));
  }

  public void onMouseDown(float x, float y) {
    eventQueue.add(new MouseDownEvent(x, y));
  }

  public void onMouseUp(float x, float y) {
    eventQueue.add(new MouseUpEvent(x, y));
  }

  public void onMouseScrolled(float x, float y, float sx, float sy) {
    eventQueue.add(new MouseScrolledEvent(x, y, sx, sy));
  }
}