package frc.shufflewood.gui;

import frc.shufflewood.gui.draw.Font;
import processing.core.PApplet;

public final class GuiStyle {
    public Font font;

    public float headerSize = 25;
    public float padding = 10;
    public float windowRounding = 15;
    public float widgetPadding = 5;
    public float selectionPadding = 5;
    public float treeArrowSize = 15;
    public float separatorSize = 15;
    public float buttonContentPadding = 5;
    public float textEditContentPadding = 5;

    public int borderColor;
    public int headerColor;
    public int headerFocusedColor;
    public int backgroundColor;
    public int textColor;
    public int selectionColor;
    public int resizeArrowColor;
    public int treeArrowColor;
    public int separatorColor;
    public int buttonColor;
    public int buttonHoverColor;
    public int buttonPressColor;
    public int buttonBorderColor;
    public int textEditColor;
    public int textEditHoverColor;
    public int textEditActiveColor;
    public int textEditBorderColor;
    public int textEditFilteredColor;
    public int textEditFilteredBorderColor;

    public GuiStyle(PApplet app) {
        // Branding colors
        int white    = app.color(255);
        int grey     = app.color(148, 153, 157);
        int purple   = app.color(81, 6, 121);
        int black    = app.color(0);

        // Additional colors
        int darkGrey = app.color(74, 76, 78);
        int red = app.color(255, 0, 0);
        int darkRed = app.color(64, 0, 0);

        borderColor = grey;
        headerColor = darkGrey;
        headerFocusedColor = purple;
        backgroundColor = black;
        textColor = white;
        selectionColor = purple;
        resizeArrowColor = darkGrey;
        treeArrowColor = white;
        separatorColor = grey;
        buttonColor = black;
        buttonHoverColor = purple;
        buttonPressColor = darkGrey;
        buttonBorderColor = grey;
        textEditColor = black;
        textEditHoverColor = purple;
        textEditActiveColor = darkGrey;
        textEditBorderColor = grey;
        textEditFilteredColor = darkRed;
        textEditFilteredBorderColor = red;
    }
}
