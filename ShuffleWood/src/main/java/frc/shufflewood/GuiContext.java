package frc.shufflewood;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import processing.core.PApplet;
import processing.core.PImage;

public final class GuiContext {
    private final PApplet app;
    private DrawList draw;
    
    private GuiInput input;
    private GuiStyle style;
    
    private Map<String, GuiWindow> windows;
    private List<GuiWindow> focusStack;
    private GuiWindow currentWindow;
    
    private float contentMinX, contentMinY;
    private float contentMaxX, contentMaxY;
    
    private float posX;
    private float posY;
    
    private float lineBeginX;
    private float widgetWidth, widgetHeight;
    
    private boolean isSameLine;
    private float sameLineSpacing;
    
    public GuiContext(PApplet app) {
        this.app = app;
        input = new GuiInput();
        
        windows = new HashMap<>();
        focusStack = new ArrayList<>();
        
        style = new GuiStyle(app);
    }
    
    public void beginFrame() {
        input.update();
        draw = new DrawList(app, style.font);
    }
    
    public void endFrame() {
        // Focus clicked window
        if (input.mouseDown || input.mouseClicked) {
            GuiWindow hovered = null;
            for (int i = focusStack.size() - 1; i >= 0; i--) {
                GuiWindow win = focusStack.get(i);
                if (input.rectHovered(win.x, win.y, win.width, win.height + style.headerSize)) {
                    hovered = win;
                    break;
                }
            }

            if (hovered != null) {
                focusStack.remove(hovered);
                focusStack.add(hovered);
            }
        }
    }
    
    // ---- Windows ----
    
    public void begin(String title) {
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
        contentMaxY = win.y + win.height - style.padding;
        
        posX = contentMinX;
        posY = contentMinY;
        
        widgetWidth = 0;
        widgetHeight = 0;
        
        lineBeginX = contentMinX;
        isSameLine = false;

        DrawList draw = win.draw;

        // Header
        draw.fillRoundRect(win.x, win.y, win.width, style.headerSize + style.windowRounding, style.windowRounding, style.headerColor);
        draw.drawText(win.title, win.x + win.width / 2f, win.y + style.headerSize / 2f, style.textColor, 0.5f, 0.5f);

        // Body
        draw.fillRoundRect(win.x, win.y + style.headerSize, win.width, win.height, style.windowRounding, style.backgroundColor);
        draw.fillRect(win.x, win.y + style.headerSize, win.width, style.windowRounding, style.backgroundColor);

        // Border
        draw.drawRoundRect(win.x, win.y, win.width, win.height + style.headerSize, style.windowRounding, style.borderColor);
        draw.drawLine(win.x, win.y + style.headerSize, win.x + win.width, win.y + style.headerSize, style.borderColor);
    }
    
    public void end() {
        GuiWindow win = currentWindow;

        // Drag header
        Vec2f d = input.rectDrag(win.x, win.y, win.width, style.headerSize);
        win.x += d.x;
        win.y += d.y;
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
        boolean[] open = currentWindow.storage.getOrSet(id, new boolean[] { false });
        return treePushState(label, open);
    }

    public void treePop() {

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

    // ---- Internal widget utilities ----

    private float getAvailWidth() {
        return contentMaxX - posX;
    }

    private boolean isItemHovered() {
        return input.rectHovered(posX, posY, widgetWidth, widgetHeight);
    }

    private boolean isItemPressed() {
        return input.rectPressed(posX, posY, widgetWidth, widgetHeight);
    }

    private boolean isItemClicked() {
        return input.rectClicked(posX, posY, widgetWidth, widgetHeight);
    }
    
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
