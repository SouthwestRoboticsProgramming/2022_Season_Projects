package frc.shufflewood.gui;

import frc.shufflewood.gui.draw.GuiDraw;
import frc.shufflewood.gui.draw.GuiDrawData;
import frc.shufflewood.gui.filter.TextFilter;
import frc.shufflewood.gui.input.GuiInput;
import processing.core.PApplet;
import processing.core.PImage;

import java.util.*;

public final class GuiContextImpl implements GuiContext {
    private static final class GuiPane {
        GuiPane parent = null;
        Rect contentRegion;
        boolean autoCalcRegionHeight;
        boolean usePadding;
        Vec2 position = new Vec2();
        Vec2 widgetSize = new Vec2();
        Vec2 sameLinePos = new Vec2();
        float lineBeginX = 0;
        float maxLineHeight = 0;

        boolean tableDrawBorder;
        boolean tableUsePadding;
        float[] tableColumnWidths;
        int tableColumnIndex;

        boolean childDrawBorder;
    }

    private static final class GuiWindow {
        GuiDraw draw;
        String title;
        Rect contentRect;
        GuiStorage storage;
    }

    private final Deque<GuiDraw> drawStack;
    private GuiDraw draw;

    private final GuiStyle style;
    private final GuiInput input;

    private final Map<String, GuiWindow> windows;
    private final Set<String> unusedWindows;
    private final List<GuiWindow> focusStack;

    private GuiWindow activeWindow;
    private GuiWindow hoveredWindow;
    private GuiPane activePane;

    public GuiContextImpl(PApplet app) {
        draw = new GuiDraw();
        drawStack = new ArrayDeque<>();

        style = new GuiStyle(app);
        input = new GuiInput();

        windows = new HashMap<>();
        unusedWindows = new HashSet<>();
        focusStack = new ArrayList<>();
    }

    private void drawPush() {
        drawStack.push(draw);
        draw = draw.split();
    }

    private void drawPop() {
        draw = drawStack.pop();
    }

    private void beginWidget() {}

    private void endWidget() {
        activePane.maxLineHeight = Math.max(activePane.maxLineHeight, activePane.widgetSize.y);

        activePane.sameLinePos.x = activePane.position.x + activePane.widgetSize.x;
        activePane.sameLinePos.y = activePane.position.y;

        activePane.position.y += activePane.maxLineHeight + style.widgetPadding;
        activePane.position.x = activePane.lineBeginX;
    }

    private Rect getWidgetRect() {
        return new Rect(
                activePane.position.x,
                activePane.position.y,
                activePane.position.x + activePane.widgetSize.x,
                activePane.position.y + activePane.widgetSize.y
        );
    }

    private boolean isItemHovered() {
        return hoveredWindow == activeWindow && input.rectHovered(getWidgetRect());
    }

    private boolean isItemPressed() {
        return isFocused() && input.rectPressed(getWidgetRect());
    }

    private boolean isItemClicked() {
        return isFocused() && input.rectClicked(getWidgetRect());
    }

    private boolean isClickedOutsideItem() {
        return !isFocused() || input.clickedOutsideRect(getWidgetRect());
    }

    private boolean isFocused() {
        return focusStack.get(focusStack.size() - 1) == activeWindow;
    }

    @Override
    public void sameLine(float spacing) {
        activePane.position.x = activePane.sameLinePos.x + spacing;
        activePane.position.y = activePane.sameLinePos.y;
    }

    @Override
    public void beginFrame(int width, int height) {
        for (String key : unusedWindows) {
            GuiWindow win = windows.remove(key);
            if (win != null)
                focusStack.remove(win);
        }
        unusedWindows.clear();
        unusedWindows.addAll(windows.keySet());

        input.update();

        draw.reset();
        draw.setSize(width, height);
        draw.setFont(style.font);

        activePane = new GuiPane();
        activePane.contentRegion = new Rect(0, 0, width, height);
        activePane.autoCalcRegionHeight = false;
    }

    @Override
    public void endFrame() {
        hoveredWindow = null;
        for (int i = focusStack.size() - 1; i >= 0; i--) {
            GuiWindow win = focusStack.get(i);
            Rect r = new Rect(win.contentRect);
            r.min.y -= style.headerSize;
            if (input.rectHovered(r)) {
                hoveredWindow = win;
                break;
            }
        }

        if ((input.mouseDown || input.mouseClicked) && hoveredWindow != null) {
            focusStack.remove(hoveredWindow);
            focusStack.add(hoveredWindow);
        }
    }

    @Override
    public GuiDrawData getDrawData() {
        for (GuiWindow win : focusStack) {
            draw.append(win.draw);
        }

        return draw.buildDrawData();
    }

    @Override
    public GuiStyle getStyle() {
        return style;
    }

    @Override
    public GuiInput getInput() {
        return input;
    }

    @Override
    public void begin(String title) {
        GuiWindow win = windows.get(title);
        if (win == null) {
            win = new GuiWindow();
            win.title = title;
            float x = (float) (Math.random() * (activePane.contentRegion.getWidth() - 400));
            float y = (float) (Math.random() * (activePane.contentRegion.getHeight() - 400));
            win.contentRect = new Rect(x, y, x + 400, y + 400);
            win.storage = new GuiStorage();
            windows.put(title, win);
        }
        unusedWindows.remove(title);

        activeWindow = win;
        win.storage.update();
        if (!focusStack.contains(win)) {
            focusStack.add(win);
        }

        drawPush();
        win.draw = draw;

        if (isFocused()) {
            // Drag header
            Vec2 drag = input.rectDrag(new Rect(win.contentRect.min.x, win.contentRect.min.y - style.headerSize, win.contentRect.max.x, win.contentRect.min.y));
            win.contentRect.move(drag);

            // Drag resize
            drag = input.rectDrag(new Rect(win.contentRect.max.x - style.padding, win.contentRect.max.y - style.padding, win.contentRect.max.x, win.contentRect.max.y));
            win.contentRect.max.x += drag.x;
            win.contentRect.max.y += drag.y;
        }

        Rect header = new Rect(win.contentRect.min.x, win.contentRect.min.y - style.headerSize, win.contentRect.max.x, win.contentRect.min.y);
        draw.fillRect(header, style.headerFocusedColor);
        draw.drawRect(header, style.borderColor);
        draw.drawText(title, new Vec2(win.contentRect.min.x + win.contentRect.getWidth() / 2, win.contentRect.min.y - style.headerSize / 2), style.textColor, new Vec2(0.5f, 0.5f));

        draw.fillRect(win.contentRect, style.backgroundColor);
        draw.drawRect(win.contentRect, style.borderColor);

        // Resize arrow
        draw.fillTriangle(
                new Vec2(win.contentRect.max.x, win.contentRect.max.y - style.padding),
                win.contentRect.max,
                new Vec2(win.contentRect.max.x - style.padding, win.contentRect.max.y),
                style.resizeArrowColor
        );

        GuiPane newPane = new GuiPane();
        newPane.parent = activePane;
        newPane.contentRegion = new Rect(
                win.contentRect.min.x + style.padding,
                win.contentRect.min.y + style.padding,
                win.contentRect.max.x - style.padding,
                win.contentRect.max.y - style.padding
        );
        newPane.lineBeginX = newPane.contentRegion.min.x;
        newPane.position = new Vec2(newPane.contentRegion.min);
        activePane = newPane;

        draw.pushClipRect(newPane.contentRegion);
    }

    @Override
    public void end() {
        draw.popClipRect();

        activePane = activePane.parent;
        if (activePane == null) {
            throw new IllegalStateException("No active draw pane!");
        }

        drawPop();
    }

    @Override
    public void setWindowPos(float x, float y) {
        float w = activeWindow.contentRect.getWidth();
        float h = activeWindow.contentRect.getHeight();

        activeWindow.contentRect.min = new Vec2(x, y);
        activeWindow.contentRect.max = new Vec2(x + w, y + h);
    }

    @Override
    public void setWindowCenterPos(float x, float y) {
        float w = activeWindow.contentRect.getWidth();
        float h = activeWindow.contentRect.getHeight();
        activeWindow.contentRect.min = new Vec2(x - w / 2, y - h / 2);
        activeWindow.contentRect.max = new Vec2(x + w / 2, y + h / 2);
    }

    @Override
    public void setWindowSize(float width, float height) {
        activeWindow.contentRect.max = new Vec2(width + activeWindow.contentRect.min.x, height + activeWindow.contentRect.min.y);
    }

    @Override
    public void setWindowHeightAuto() {
        activeWindow.contentRect.max.y = activePane.position.y - style.widgetPadding + style.padding;
    }

    @Override
    public Vec2 getAvailableContentSize() {
        return new Vec2(
                activePane.contentRegion.max.x - activePane.position.x,
                activePane.contentRegion.max.y - activePane.position.y
        );
    }

    @Override
    public void beginChild(Vec2 size, boolean drawBorder, boolean usePadding) {
        beginWidget();

        activePane.childDrawBorder = drawBorder;

        GuiPane newPane = new GuiPane();
        newPane.parent = activePane;
        newPane.usePadding = usePadding;
        if (usePadding) {
            newPane.contentRegion = new Rect(
                    activePane.position.x + style.padding, activePane.position.y + style.padding,
                    activePane.position.x + size.x - style.padding,
                    (size.y > 0 ? activePane.position.y + size.y : activePane.position.y + getAvailableContentSize().y) - style.padding
            );
        } else {
            newPane.contentRegion = new Rect(
                    activePane.position.x, activePane.position.y,
                    activePane.position.x + size.x,
                    size.y > 0 ? activePane.position.y + size.y : activePane.position.y + getAvailableContentSize().y
            );
        }
        if (size.y < 0)
            newPane.autoCalcRegionHeight = true;
        newPane.lineBeginX = newPane.contentRegion.min.x;
        newPane.position = new Vec2(newPane.contentRegion.min);
        activePane = newPane;

        if (size.y < 0) {
            Vec2 contentSize = getAvailableContentSize();
            draw.pushClipRect(new Rect(
                    newPane.contentRegion.min,
                    new Vec2(
                            newPane.contentRegion.min.x + contentSize.x,
                            newPane.contentRegion.min.y + contentSize.y
                    )
            ));
        } else {
            draw.pushClipRect(newPane.contentRegion);
        }
    }

    @Override
    public void endChild() {
        draw.popClipRect();

        GuiPane childPane = activePane;
        activePane = activePane.parent;
        if (activePane == null) {
            throw new IllegalStateException("No active draw pane!");
        }

        if (childPane.autoCalcRegionHeight) {
            childPane.contentRegion.max.y = childPane.position.y - style.widgetPadding;
        }

        if (childPane.usePadding) {
            activePane.widgetSize = new Vec2(
                    childPane.contentRegion.getWidth() + style.padding * 2,
                    childPane.contentRegion.getHeight() + style.padding * 2
            );
        } else {
            activePane.widgetSize = new Vec2(
                    childPane.contentRegion.getWidth(),
                    childPane.contentRegion.getHeight()
            );
        }

        if (activePane.childDrawBorder) {
            Rect borderRect = new Rect(
                    activePane.position.x,
                    activePane.position.y,
                    activePane.position.x + activePane.widgetSize.x,
                    activePane.position.y + activePane.widgetSize.y
            );
            draw.drawRect(borderRect, style.borderColor);
        }

        endWidget();
    }

    @Override
    public void text(String text, Object... fmtArgs) {
        text = format(text, fmtArgs);

        beginWidget();

        activePane.widgetSize.x = style.font.getWidth(text);
        activePane.widgetSize.y = style.font.getHeight();

        draw.drawText(text, activePane.position, style.textColor);

        endWidget();
    }

    @Override
    public void selectableText(String text, boolean[] selected) {
        beginWidget();

        activePane.widgetSize.x = style.font.getWidth(text) + style.selectionPadding * 2;
        activePane.widgetSize.y = style.font.getHeight() + style.selectionPadding * 2;

        if (isItemClicked()) {
            selected[0] = true;
        }

        if (selected[0]) {
            draw.fillRect(getWidgetRect(), style.selectionColor);
        }

        draw.drawText(text, new Vec2(activePane.position.x + style.selectionPadding, activePane.position.y + activePane.widgetSize.y / 2), style.textColor, new Vec2(0, 0.5f));

        endWidget();
    }

    @Override
    public void separator() {
        beginWidget();

        GuiPane p = activePane;

        p.widgetSize.x = getAvailableContentSize().x;
        p.widgetSize.y = style.separatorSize;

        draw.drawLine(
                new Vec2(
                        p.position.x,
                        p.position.y + style.separatorSize / 2f
                ),
                new Vec2(
                        p.contentRegion.max.x,
                        p.position.y + style.separatorSize / 2f
                ),
                style.separatorColor
        );

        endWidget();
    }

    @Override
    public boolean button(String label, Vec2 size) {
        beginWidget();
        GuiPane p = activePane;

        p.widgetSize.x = size.x >= 0 ? size.x : getAvailableContentSize().x;
        p.widgetSize.y = size.y >= 0 ? size.y : style.font.getHeight() + style.buttonContentPadding * 2;

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

        Rect r = new Rect(new Vec2(p.position), new Vec2(p.position.x + p.widgetSize.x, p.position.y + p.widgetSize.y));
        draw.fillRect(r, color);
        draw.drawRect(r, style.buttonBorderColor);
        draw.drawText(label, new Vec2(p.position.x + p.widgetSize.x / 2, p.position.y + p.widgetSize.y / 2), style.textColor, new Vec2(0.5f, 0.5f));

        endWidget();

        return clicked;
    }

    @Override
    public void image(PImage image, float width, float height) {
        beginWidget();
        GuiPane p = activePane;

        p.widgetSize.x = width;
        p.widgetSize.y = height;

        Rect r = new Rect(new Vec2(p.position), new Vec2(p.position.x + width, p.position.y + height));
        Rect uv = new Rect(0, 0, image.width, image.height);
        draw.textureRect(r, uv, image, 0xffffffff);
        draw.drawRect(r, style.borderColor);

        endWidget();
    }

    // TODO: Make better
    @Override
    public boolean checkbox(boolean[] checked) {
        Vec2 size = new Vec2(50, style.font.getHeight() + style.buttonContentPadding * 2);
        boolean clicked = button(checked[0] ? "Yes" : "No", size);

        if (clicked) {
            checked[0] = !checked[0];
        }

        return clicked;
    }

    @Override
    public void pie(float width, float height, float[] values, int[] colors) {
        beginWidget();
        GuiPane p = activePane;

        float valueTotal = 0;
        for (float value : values) {
            valueTotal += value;
        }

        Rect rect = new Rect(new Vec2(p.position), new Vec2(width + p.position.x, width + p.position.y));

        float angleAcc = 0;
        for (int i = 0; i < values.length; i++) {
            float value = values[i];
            float angle = (float) (value / valueTotal * Math.PI * 2);
            float minAngle = angleAcc;
            float maxAngle = angleAcc += angle;

            draw.fillSector(rect, minAngle, maxAngle, colors[i]);
        }

        endWidget();
    }

    private static final class TextEditState {
        boolean editing;
        StringBuffer editBuffer;
        int cursorPos;

        private TextEditState() {
            editing = false;
            editBuffer = new StringBuffer();
            cursorPos = 0;
        }
    }

    @Override
    public boolean editString(StringBuffer buf, Object id, TextFilter filter) {
        beginWidget();

        TextEditState state = activeWindow.storage.getOrSet(id, TextEditState::new);
        String str = state.editing ? state.editBuffer.toString() : buf.toString();

        activePane.widgetSize.x = getAvailableContentSize().x;
        activePane.widgetSize.y = style.font.getHeight() + style.textEditContentPadding * 2;

        boolean hovered = isItemHovered();
        boolean clicked = isItemClicked();
        boolean clickedOutside = isClickedOutsideItem();

        if (state.editing) {
            state.editBuffer.append(input.getTextInput());
        }
        boolean allowed = filter.isAllowed(state.editBuffer.toString());
        boolean edited = false;

        if (clicked) {
            state.editing = true;
            state.editBuffer.replace(0, state.editBuffer.length(), "");
            state.cursorPos = 0;
        } else if (state.editing && clickedOutside) {
            state.editing = false;
            if (allowed && state.editBuffer.length() != 0) {
                buf.replace(0, buf.length(), state.editBuffer.toString());
                edited = true;
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

        Rect r = getWidgetRect();
        draw.fillRect(r, backgroundColor);
        draw.drawRect(r, borderColor);
        draw.drawText(str, new Vec2(activePane.position.x + style.textEditContentPadding, activePane.position.y + activePane.widgetSize.y / 2), style.textColor, new Vec2(0, 0.5f));

        endWidget();

        return edited;
    }

    @Override
    public boolean editInt(int[] value, Object id, TextFilter filter) {
        StringBuffer buf = new StringBuffer(String.valueOf(value[0]));
        boolean edited = editString(buf, id, filter);
        value[0] = Integer.parseInt(buf.toString());
        return edited;
    }

    @Override
    public boolean editDouble(double[] value, Object id, TextFilter filter) {
        StringBuffer buf = new StringBuffer(String.valueOf(value[0]));
        boolean edited = editString(buf, id, filter);
        value[0] = Double.parseDouble(buf.toString());
        return edited;
    }

    @Override
    public boolean treePushState(String label, boolean[] open) {
        return false;
    }

    @Override
    public boolean treePush(String label, Object id) {
        return false;
    }

    @Override
    public void treePop() {

    }

    @Override
    public void beginTable(boolean drawBorder, boolean usePadding, float... columnWeights) {
        float totalWeight = 0;
        for (float weight : columnWeights) {
            totalWeight += weight;
        }

        activePane.tableDrawBorder = drawBorder;
        activePane.tableUsePadding = usePadding;

        float availWidth = activePane.contentRegion.getWidth();

        activePane.tableColumnWidths = new float[columnWeights.length];
        for (int i = 0; i < columnWeights.length; i++) {
            activePane.tableColumnWidths[i] = columnWeights[i] / totalWeight * availWidth;
        }

        activePane.tableColumnIndex = 0;
        beginChild(new Vec2(activePane.tableColumnWidths[0], -1), drawBorder, usePadding);
    }

    @Override
    public void tableNextColumn() {
        endChild();

        activePane.tableColumnIndex++;
        if (activePane.tableColumnIndex == activePane.tableColumnWidths.length) {
            activePane.tableColumnIndex = 0;
        } else {
            sameLine(0);
        }

        beginChild(new Vec2(activePane.tableColumnWidths[activePane.tableColumnIndex], -1), activePane.tableDrawBorder, activePane.tableUsePadding);
    }

    @Override
    public void endTable() {
        endChild();
    }

    private String format(String str, Object... fmtArgs) {
        return String.format(str, fmtArgs);
    }
}
