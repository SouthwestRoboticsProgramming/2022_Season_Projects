package frc.shufflewood.gui;

import frc.shufflewood.gui.draw.GuiDrawData;
import frc.shufflewood.gui.filter.AnyFilter;
import frc.shufflewood.gui.filter.IntFilter;
import frc.shufflewood.gui.filter.TextFilter;
import processing.core.PImage;

public interface GuiContext {
    // Default text filters
    TextFilter FILTER_ANY = new AnyFilter();
    TextFilter FILTER_INT = new IntFilter();

    // Frame
    void beginFrame(int width, int height);
    void endFrame();

    // Getters
    GuiDrawData getDrawData();
    GuiStyle getStyle();
    GuiInput getInput();

    // Windows
    void begin(String title);
    void end();
    void setWindowPos(float x, float y);
    void setWindowCenterPos(float x, float y);
    void setWindowSize(float width, float height);
    void setWindowHeightAuto();

    // Regions
    Vec2 getAvailableContentSize();

    // Child panels
    default void beginChild(Vec2 size) { beginChild(size, true, true); }
    default void beginChild(Vec2 size, boolean drawBorder) { beginChild(size, drawBorder, drawBorder); }
    void beginChild(Vec2 size, boolean drawBorder, boolean usePadding);
    void endChild();

    // Layout
    default void sameLine() { sameLine(getStyle().widgetPadding); }
    void sameLine(float spacing);

    // Widgets
    void text(String text, Object... fmtArgs);
    void selectableText(String text, boolean[] selected);
    void separator();
    default boolean button(String label) { return button(label, new Vec2(-1, -1)); }
    boolean button(String label, Vec2 size);
    default void image(PImage image) { image(image, image.width, image.height); }
    void image(PImage image, float width, float height);

    // Text edit
    // TODO: Maybe use String[] instead?
    default void editString(StringBuffer buf) { editString(buf, buf, FILTER_ANY); }
    default void editString(StringBuffer buf, Object id) { editString(buf, id, FILTER_ANY); }
    void editString(StringBuffer buf, Object id, TextFilter filter);
    default void editInt(int[] value) { editInt(value, value); }
    default void editInt(int[] value, Object id) { editInt(value, id, FILTER_INT); }
    void editInt(int[] value, Object id, TextFilter filter);

    // Trees
    boolean treePushState(String label, boolean[] open);
    default boolean treePush(String label) { return treePush(label, label); }
    boolean treePush(String label, Object id);
    void treePop();

    // Tables
    default void beginTable(float... columnWeights) { beginTable(true, true, columnWeights); }
    default void beginTable(boolean drawBorder, float... columnWeights) { beginTable(drawBorder, drawBorder, columnWeights); }
    void beginTable(boolean drawBorder, boolean usePadding, float... columnWeights);
    void tableNextColumn();
    void endTable();
}
