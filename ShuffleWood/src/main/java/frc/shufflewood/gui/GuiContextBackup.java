package frc.shufflewood.gui;

import frc.shufflewood.Rectangle;
import frc.shufflewood.Vec2f;
import frc.shufflewood.draw.DrawList;
import frc.shufflewood.draw.Font;
import frc.shufflewood.gui.filter.AnyFilter;
import frc.shufflewood.gui.filter.IntegerFilter;
import frc.shufflewood.gui.filter.TextFilter;
import processing.core.PApplet;
import processing.core.PImage;

import java.util.*;

public final class GuiContextBackup {
    private final PApplet app;
    private DrawList draw;

    private GuiInput input;
    private GuiStyle style;

    private Map<String, GuiWindow> windows;
    private Set<String> unusedWindows;
    private List<GuiWindow> focusStack;
    private GuiWindow currentWindow;
    private GuiWindow hoveredWindow;

    private float contentMinX, contentMinY;
    private float contentMaxX, contentMaxY;

    private float posX;
    private float posY;

    private float lineBeginX;
    private float widgetWidth, widgetHeight;

    private boolean isSameLine;
    private float sameLineSpacing;

    public GuiContextBackup(PApplet app) {
        this.app = app;
        input = new GuiInput();
        
        windows = new HashMap<>();
        unusedWindows = new HashSet<>();
        focusStack = new ArrayList<>();
        
        style = new GuiStyle(app);
    }
    
    public void beginFrame() {
        for (String key : unusedWindows) {
            System.out.println(key);
            GuiWindow win = windows.remove(key);
            if (win != null)
                focusStack.remove(win);
        }
        unusedWindows.clear();
        unusedWindows.addAll(windows.keySet());

        input.update();
        draw = new DrawList(app, style.font);
    }
    
    public void endFrame() {
        hoveredWindow = null;
        for (int i = focusStack.size() - 1; i >= 0; i--) {
            GuiWindow win = focusStack.get(i);
            if (input.rectHovered(win.x, win.y, win.width, win.height + style.headerSize)) {
                hoveredWindow = win;
                break;
            }
        }

        // Focus clicked window
        if (input.mouseDown || input.mouseClicked) {
            if (hoveredWindow != null) {
                focusStack.remove(hoveredWindow);
                focusStack.add(hoveredWindow);
            }
        }
    }
    
    // ---- Windows ----
    
    public void begin(String title) {
        unusedWindows.remove(title);

        GuiWindow win = windows.get(title);
        if (win == null) {
            win = new GuiWindow();
            win.title = title;
            win.x = app.random(0, app.width - 200);
            win.y = app.random(0, app.height - 400);
            win.width = 200;
            win.height = 350;
            win.storage = new GuiStorage();
            windows.put(title, win);
        }
        if (!focusStack.contains(win)) {
            focusStack.add(win);
        }
        win.draw = new DrawList(app, style.font);
        win.storage.update();
        
        currentWindow = win;
        
        contentMinX = win.x + style.padding;
        contentMinY = win.y + style.headerSize + style.padding;
        contentMaxX = win.x + win.width - style.padding;
        contentMaxY = win.y + style.headerSize + win.height - style.padding;
        
        posX = contentMinX;
        posY = contentMinY;
        
        widgetWidth = 0;
        widgetHeight = 0;
        
        lineBeginX = contentMinX;
        isSameLine = false;

        DrawList draw = win.draw;

        // Header
        draw.fillRoundRect(win.x, win.y, win.width, style.headerSize + style.windowRounding, style.windowRounding, isFocused() ? style.headerFocusedColor : style.headerColor);
        draw.drawText(win.title, win.x + win.width / 2f, win.y + style.headerSize / 2f, style.textColor, 0.5f, 0.5f);

        // Body
        draw.fillRoundRect(win.x, win.y + style.headerSize, win.width, win.height, style.windowRounding, style.backgroundColor);
        draw.fillRect(win.x, win.y + style.headerSize, win.width, style.windowRounding, style.backgroundColor);

        // Border
        draw.drawRoundRect(win.x, win.y, win.width, win.height + style.headerSize, style.windowRounding, style.borderColor);
        draw.drawLine(win.x, win.y + style.headerSize, win.x + win.width, win.y + style.headerSize, style.borderColor);

        draw.clip(contentMinX - 2, contentMinY - 2, contentMaxX + 2, contentMaxY + 2);
    }
    
    public void end() {
        GuiWindow win = currentWindow;
        win.draw.noClip();

        // Drag header
        if (isFocused()) {
            Vec2f d = input.rectDrag(win.x, win.y, win.width, style.headerSize);
            win.x += d.x;
            win.y += d.y;
        }
    }

    public void setWindowPos(float x, float y) {
        currentWindow.x = x;
        currentWindow.y = y;
    }

    public void setWindowCenterPos(float x, float y) {
        currentWindow.x = x - currentWindow.width / 2;
        currentWindow.y = y - currentWindow.height / 2;
    }

    public void setWindowSize(float width, float height) {
        currentWindow.width = width;
        currentWindow.height = height;
    }
    
    // ---- Widgets ----
    
    public void text(String text) {
        beginWidget();
        
        DrawList draw = currentWindow.draw;
        Font font = style.font;
        draw.drawText(text, posX, posY, style.textColor);
        
        widgetWidth = font.getWidth(text);
        widgetHeight = font.getHeight();
        
        endWidget();
    }
    
    public void separator() {
        beginWidget();
        
        widgetWidth = getAvailWidth();
        widgetHeight = style.separatorSize;
        
        DrawList draw = currentWindow.draw;
        draw.drawLine(posX, posY + style.separatorSize / 2f, contentMaxX, posY + style.separatorSize / 2f, style.separatorColor);
        
        endWidget();
    }

    public boolean button(String label) {
        beginWidget();

        widgetWidth = getAvailWidth();
        widgetHeight = style.font.getHeight() + style.buttonContentPadding * 2;

        boolean hovered = isItemHovered();
        boolean down = isItemPressed();
        boolean clicked = isItemClicked();

        int color;
        if (down) {
            color = style.buttonPressColor;
        } else if (hovered) {
            color = style.buttonHoverColor;
        } else {
            color = style.buttonColor;
        }
        
        DrawList draw = currentWindow.draw;
        draw.fillRect(posX, posY, widgetWidth, widgetHeight, color);
        draw.drawRect(posX, posY, widgetWidth, widgetHeight, style.buttonBorderColor);
        draw.drawText(label, posX + widgetWidth / 2, posY + widgetHeight / 2, style.textColor, 0.5f, 0.5f);
        
        endWidget();

        return clicked;
    }

    public boolean treePushState(String label, boolean[] open) {
        beginWidget();

        widgetWidth = style.font.getWidth(label) + style.treeArrowSize + style.widgetPadding;
        widgetHeight = Math.max(style.treeArrowSize, style.font.getHeight());

        if (isItemClicked()) {
            open[0] = !open[0];
        }
        
        DrawList draw = currentWindow.draw;
        if (open[0]) {
            draw.fillTriangle(
                posX, posY,
                posX + style.treeArrowSize / 2f, posY + style.treeArrowSize,
                posX + style.treeArrowSize, posY,
                style.treeArrowColor
            );

            indent(style.treeArrowSize + style.widgetPadding);
        } else {
            draw.fillTriangle(
                posX, posY,
                posX + style.treeArrowSize, posY + style.treeArrowSize / 2f,
                posX, posY + style.treeArrowSize,
                style.treeArrowColor
            );
        }

        draw.drawText(label, posX + style.treeArrowSize + style.widgetPadding, posY + style.treeArrowSize / 2f, style.textColor, 0.5f);
        
        endWidget();
        return open[0];
    }

    public boolean treePush(String label) { return treePush(label, label); }
    public boolean treePush(String label, Object id) {
        boolean[] open = currentWindow.storage.getOrSet(id, () -> new boolean[] { false });
        return treePushState(label, open);
    }

    public void treePop() {
        unindent(style.treeArrowSize + style.widgetPadding);
    }

    public void image(PImage image) { image(image, image.width, image.height); }
    public void image(PImage image, float width, float height) {
        beginWidget();

        widgetWidth = width;
        widgetHeight = height;

        DrawList draw = currentWindow.draw;
        draw.fillTexturedRect(posX, posY, width, height, 0, 0, image.width, image.height, image, 0xffffffff);
        draw.drawRect(posX, posY, width, height, style.borderColor);

        endWidget();
    }

    // Built-in text filters
    private static final TextFilter FILTER_ANY = new AnyFilter();
    private static final TextFilter FILTER_INT = new IntegerFilter();

    private static class TextEditState {
        boolean editing;
        StringBuffer editBuffer;
        int cursorPos;

        private TextEditState() {
            editing = false;
            editBuffer = new StringBuffer();
            cursorPos = 0;
        }
    }

    public void editString(StringBuffer str) { editString(str, str, FILTER_ANY); }
    public void editString(StringBuffer buf, Object id) { editString(buf, id, FILTER_ANY); }
    public void editString(StringBuffer buf, Object id, TextFilter filter) {
        beginWidget();

        TextEditState state = currentWindow.storage.getOrSet(id, TextEditState::new);
        String str = state.editing ? state.editBuffer.toString() : buf.toString();

        widgetWidth = getAvailWidth();
        widgetHeight = style.font.getHeight() + style.textEditContentPadding * 2;

        boolean hovered = isItemHovered();
        boolean clicked = isItemClicked();
        boolean clickedOutside = isClickedOutsideItem();

        if (state.editing) {
            state.editBuffer.append(input.getTextInput());
        }
        boolean allowed = filter.isAllowed(state.editBuffer.toString());

        if (clicked) {
            state.editing = true;
            state.editBuffer.replace(0, state.editBuffer.length(), "");
            state.cursorPos = 0;
        } else if (state.editing && clickedOutside) {
            state.editing = false;
            if (allowed && state.editBuffer.length() != 0) {
                buf.replace(0, buf.length(), state.editBuffer.toString());
            }
        }

        // Determine background and border colors
        int backgroundColor, borderColor;
        if (state.editing) {
             if (allowed) {
                 borderColor = style.textEditBorderColor;
                 backgroundColor = style.textEditActiveColor;
             } else {
                 borderColor = style.textEditFilteredBorderColor;
                 backgroundColor = style.textEditFilteredColor;
             }
        } else {
            borderColor = style.textEditBorderColor;

            if (hovered) {
                backgroundColor = style.textEditHoverColor;
            } else {
                backgroundColor = style.textEditColor;
            }
        }

        DrawList draw = currentWindow.draw;
        draw.fillRect(posX, posY, widgetWidth, widgetHeight, backgroundColor);
        draw.drawRect(posX, posY, widgetWidth, widgetHeight, borderColor);
        draw.drawText(str, posX + style.textEditContentPadding, posY + widgetHeight / 2, style.textColor, 0.5f);

        endWidget();
    }

    public void editInt(int[] value) { editInt(value, value); }
    public void editInt(int[] value, Object id) { editInt(value, id, FILTER_INT); }
    public void editInt(int[] value, Object id, TextFilter filter) {
        StringBuffer buf = new StringBuffer(String.valueOf(value[0]));
        editString(buf, id, filter);
        value[0] = Integer.parseInt(buf.toString());
    }

    // ---- Internal widget utilities ----

    private float getAvailWidth() {
        return contentMaxX - posX;
    }

    private boolean isItemHovered() {
        return hoveredWindow == currentWindow && input.rectHovered(posX, posY, widgetWidth, widgetHeight);
    }

    private boolean isItemPressed() {
        return isFocused() && input.rectPressed(posX, posY, widgetWidth, widgetHeight);
    }

    private boolean isItemClicked() {
        return isFocused() && input.rectClicked(posX, posY, widgetWidth, widgetHeight);
    }

    private boolean isClickedOutsideItem() {
        return !isFocused() || input.clickedOutsideRect(posX, posY, widgetWidth, widgetHeight);
    }

    private boolean isFocused() { return focusStack.get(focusStack.size() - 1) == currentWindow; }
    
    // ---- Layout ----
    
    public void sameLine() { sameLine(style.widgetPadding); }
    public void sameLine(float spacing) {
        isSameLine = true;
        sameLineSpacing = spacing;
    }
    
    public void indent() { indent(20); }
    public void indent(float amount) {
        lineBeginX += amount;
    }
    
    public void unindent() { unindent(20); }
    public void unindent(float amount) {
        lineBeginX -= amount;
    }

    // Tables
    public void beginTable(float... columnWeights) {}
    public void endTable() {}
    public void tableNextCell() {}
    public void tableNextRow() {}
    public void tableHeaderRow() {}
    
    // Internal
    private void beginWidget() {
        if (isSameLine) {
            posX += widgetWidth + sameLineSpacing;
        } else {
            posY += widgetHeight + style.widgetPadding;
            posX = lineBeginX;
        }
    }

    // Internal
    private void endWidget() {
        isSameLine = false;
    }
    
    // ---- Getters ----
    
    public DrawList getDrawList() {
        for (GuiWindow win : focusStack) {
            draw.append(win.draw);
        }
        return draw;
    }
    
    public GuiInput getInput() {
        return input;
    }
    
    public GuiStyle getStyle() {
        return style;
    }

    public Rectangle getContentRegion() {
        return new Rectangle(contentMinX, contentMinY, contentMaxX - contentMinX, contentMaxY - contentMinY);
    }
}
