package frc.shuffleplank.gui;

import frc.shuffleplank.ShufflePlank;
import frc.shuffleplank.gui.draw.DrawList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GuiContext {
    public static float clamp(float val, float min, float max) {
        if (val < min) return min;
        if (val > max) return max;
        return val;
    }

    private final GuiInput input;
    private final GuiStyle style;
    private final Map<String, Window> windows;
    private final List<Window> focusStack;

    private float displayWidth;
    private float displayHeight;
    private Window currentWindow;
    private Window hoveredWindow;
    private float posX;
    private float posY;
    private float maxX;
    private float maxY;
    private boolean anyWindowFocused = false;

    public GuiContext() {
        input = new GuiInput();
        style = new GuiStyle();
        windows = new HashMap<String, Window>();
        focusStack = new ArrayList<Window>();
    }

    private String nf(float n, int l, int r) {
        return ShufflePlank.nf(n, l, r);
    }

    public void showDebugWindow() {
        begin("GUI Engine Debug");
        {
            text("Active windows:");
            indent();
            for (Window win : focusStack) {
                text(win.name);
                indent();
                {
                    text("Position: (" + nf(win.x, 0, 1) + ", " + nf(win.y, 0, 1) + ")");
                    text("Size: (" + nf(win.w, 0, 1) + ", " + nf(win.h, 0, 1) + ")");
                    text("Draw commands: " + win.draw.cmds.size());
                    if (win.storage.size() > 0) {
                        text("Storage:");
                        indent();
                        {
                            for (Map.Entry<Object, Object> entry : win.storage.entrySet()) {
                                text(entry.getKey().toString() + " -> " + entry.getValue().toString());
                            }
                        }
                        unindent();
                    }
                }
                unindent();
            }
            unindent();
            spacing();
            text("Hovering window: " + (hoveredWindow == null ? "null" : hoveredWindow.name));

            Window focusedWindow = getFocused();
            text("Focused window: " + (focusedWindow == null ? "null" : focusedWindow.name));
        }
        end();
    }

    // Query functions
    public boolean isAnyWindowFocused() {
        return anyWindowFocused;
    }

    public void beginFrame(float displayWidth, float displayHeight) {
        this.displayWidth = displayWidth;
        this.displayHeight = displayHeight;
        posX = 0;
        posY = 0;
        maxX = 0;
        maxY = 0;
        input.update();
        hoveredWindow = getWindowAtPoint(input.lastMouseX, input.lastMouseY);
        if (input.mouseDown) {
            if (hoveredWindow != null) {
                // Move the hovered window to the top of the focus stack to give it focus
                focusStack.remove(hoveredWindow);
                focusStack.add(hoveredWindow);
                anyWindowFocused = true;
            } else {
                // If a non-window area is clicked, have nothing focused
                anyWindowFocused = false;
            }
        }
    }

    public void endFrame() {

    }

    public void begin(String title) {
        // Get window data
        Window win = windows.get(title);
        if (win == null) {
            win = new Window(title);
            windows.put(title, win);
        }
        if (!focusStack.contains(win)) {
            focusStack.add(win);
        }
        currentWindow = win;

        win.draw = new DrawList();

        DrawList d = win.draw;
        float x = win.x;
        float y = win.y;
        float w = win.w;
        float h = win.h;

        // Draw header
        //d.fillRect(x, y, w, style.headerSize, isWindowFocused(win) ? style.headerActiveColor : style.headerColor);
        //d.drawRect(x, y, w, style.headerSize, style.borderColor);

        float r = style.windowRounding;
        d.fillRoundRect(x, y, w, style.headerSize + r, r, isWindowFocused(win) ? style.headerActiveColor : style.headerColor);
        drawTextVertCenter(title, x + style.padding, y + style.headerSize / 2.0f, style.textColor);
        y += style.headerSize;

        // Draw background
        d.fillRect(x, y, w, r, style.backgroundColor);
        d.fillRoundRect(x, y, w, h, r, style.backgroundColor);

        // Draw border
        d.drawRoundRect(x, y - style.headerSize, w, h + style.headerSize, r, style.borderColor);
        d.drawLine(x, y, x + w - 0.5f, y, style.borderColor);

        // Truncate position to avoid clipping the outer pixels
        posX = (int) (x + style.padding);
        posY = (int) (y + style.padding);
        maxX = (int) (posX + w - style.padding * 2);
        maxY = (int) (posY + h - style.padding * 2);

        // Ensure nothing is rendered outside of the content region
        // Extra pixel is given to not clip borders
        d.setClip(posX - 1, posY - 1, maxX - posX + 1, maxY - posY + 1);
    }

    public void end() {
        // Only process input if window is hovered
        if (hoveredWindow == currentWindow) {
            // Drag window header to move
            Vec2f headerDrag = input.getDragInRect(currentWindow.x, currentWindow.y, currentWindow.w, style.headerSize);
            currentWindow.x += headerDrag.x;
            currentWindow.y += headerDrag.y;

            // Drag bottom right corner to resize
            float s = style.resizeGrabSize;
            Vec2f resizeDrag = input.getDragInRect(maxX + style.padding - s, maxY + style.padding - s, s, s);
            currentWindow.w += resizeDrag.x;
            currentWindow.h += resizeDrag.y;
        }

        // Ensure window is within size constraints
        currentWindow.w = clamp(currentWindow.w, style.windowRounding * 2, displayWidth);
        currentWindow.h = clamp(currentWindow.h, style.windowRounding * 2, displayHeight - style.headerSize);

        // Ensure window is fully on the screen
        currentWindow.x = clamp(currentWindow.x, 0, displayWidth - currentWindow.w);
        currentWindow.y = clamp(currentWindow.y, 0, displayHeight - currentWindow.h - style.headerSize);

        // Disable content clipping
        currentWindow.draw.noClip();
    }

    public void spacing() { spacing(style.lineSpacing); }
    public void spacing(float amount) {
        posY += amount;
    }

    public void separator() {
        float y = posY + style.separatorSize / 2.0f;

        currentWindow.draw.drawLine(posX, y, maxX, y, style.separatorColor);

        posY += style.separatorSize + style.lineSpacing;
    }

    public void doubleSeparator() {
        float y = posY + style.separatorSize / 2.0f;

        DrawList d = currentWindow.draw;
        d.drawLine(posX, y - 1, maxX, y - 1, style.separatorColor);
        d.drawLine(posX, y + 1, maxX, y + 1, style.separatorColor);

        posY += style.separatorSize + style.lineSpacing;
    }

    public void indent() { indent(style.indentAmt); }
    public void indent(float amount) {
        posX += amount;
    }

    public void unindent() { unindent(style.indentAmt); }
    public void unindent(float amount) {
        posX -= amount;
    }

    public void text(String text) {
        drawText(text, posX, posY, style.textColor);
        posY += textHeight() + style.lineSpacing;
    }

    public void textWrap(String text) { textWrap(text, true); }
    public void textWrap(String text, boolean wrapOnWordBoundary) {
        float maxW = maxX - posX;

        int lineStartIndex = 0;
        for (int i = 0; i < text.length(); i++) {
            int nextIdx;
            if (wrapOnWordBoundary) {
                nextIdx = text.indexOf(' ', i);
                if (nextIdx == -1) {
                    nextIdx = text.length();
                }
            } else {
                nextIdx = i + 1;
            }

            String advance = text.substring(lineStartIndex, nextIdx);
            if (textWidth(advance) > maxW) {
                text(text.substring(lineStartIndex, i));
                lineStartIndex = i;
            }
        }
        text(text.substring(lineStartIndex));
    }

    public boolean button(String label) {
        float w = maxX - posX;
        float h = textHeight() + style.buttonContentPadding * 2;

        boolean hovered = hoveredWindow == currentWindow && input.mouseInRect(posX, posY, w, h);
        boolean clicked = hovered && input.mouseClicked;
        boolean pressed = clicked || (hovered && input.mouseDown);

        int fill = pressed ? style.buttonPressColor : (hovered ? style.buttonHoverColor : style.buttonColor);

        DrawList d = currentWindow.draw;
        d.fillRoundRect(posX, posY, w, h, style.buttonRounding, fill);
        d.drawRoundRect(posX, posY, w, h, style.buttonRounding, style.buttonBorderColor);

        float labelWidth = textWidth(label);
        drawTextVertCenter(label, posX + w / 2.0f - labelWidth / 2.0f, posY + h / 2.0f, style.textColor);

        posY += h + style.lineSpacing;

        return clicked;
    }

    public boolean checkbox(boolean[] checked) {
        float s = style.checkboxPadding * 2 + textHeight();

        boolean hovered = hoveredWindow == currentWindow && input.mouseInRect(posX, posY, s, s);
        boolean clicked = hovered && input.mouseClicked;
        boolean pressed = clicked || (hovered && input.mouseDown);

        if (clicked) {
            checked[0] = !checked[0];
        }

        int fill = pressed ? style.checkboxPressColor : (hovered ? style.checkboxHoverColor : style.checkboxColor);

        DrawList d = currentWindow.draw;
        d.fillRoundRect(posX, posY, s, s, style.buttonRounding, fill);
        d.drawRoundRect(posX, posY, s, s, style.buttonRounding, style.checkboxBorderColor);

        if (checked[0]) {
            // Draw the check
            d.drawLine(posX + s * 0.2f, posY + s * 0.5f, posX + s * 0.33f, posY + s * 0.8f, style.checkboxCheckColor);
            d.drawLine(posX + s * 0.33f, posY + s * 0.8f, posX + s * 0.8f, posY + s * 0.2f, style.checkboxCheckColor);
        }

        return clicked;
    }

    public GuiInput getInput() {
        return input;
    }

    public GuiStyle getStyle() {
        return style;
    }

    public DrawList getDrawList() {
        DrawList draw = new DrawList();
        for (Window window : focusStack) {
            draw.add(window.draw);
        }
        return draw;
    }

    private Window getWindowAtPoint(float x, float y) {
        for (int i = focusStack.size() - 1; i >= 0; i--) {
            Window win = focusStack.get(i);
            if (x > win.x && y > win.y && x < win.x + win.w && y < win.y + win.h + style.headerSize) {
                return win;
            }
        }

        return null;
    }

    private Window getFocused() {
        if (!anyWindowFocused) {
            return null;
        }

        return focusStack.get(focusStack.size() - 1);
    }

    private boolean isWindowFocused(Window win) {
        return anyWindowFocused && getFocused() == win;
    }

    private void drawText(String str, float x, float y, int col) {
        currentWindow.draw.drawText(str, x, y + textHeight(), col);
    }

    private void drawTextVertCenter(String str, float x, float y, int col) {
        drawText(str, x, y - textHeight() / 2.0f, col);
    }

    private float textHeight() {
        return ShufflePlank.instance.textAscent();
    }

    private float textWidth(String str) {
        return ShufflePlank.instance.textWidth(str);
    }
}
