package frc.shufflewood;

import processing.core.PApplet;

public final class GuiStyle {
    public Font font;
    
    public float headerSize = 25;
    public float padding = 10;
    public float windowRounding = 15;
    public float widgetPadding = 5;
    public float treeArrowSize = 15;
    public float separatorSize = 15;
    public float buttonContentPadding = 5;
    
    public int borderColor;
    public int headerColor;
    public int backgroundColor;
    public int textColor;
    public int treeArrowColor;
    public int separatorColor;
    public int buttonColor;
    public int buttonHoverColor;
    public int buttonPressColor;
    public int buttonBorderColor;
    
    public GuiStyle(PApplet app) {
        int white    = app.color(255);
        int grey     = app.color(148, 153, 157);
        int darkGrey = app.color(74, 76, 78);
        int purple   = app.color(81, 6, 121);
        int black    = app.color(0);
        
        borderColor = white;
        headerColor = purple;
        backgroundColor = black;
        textColor = white;
        treeArrowColor = white;
        separatorColor = grey;
        buttonColor = black;
        buttonHoverColor = purple;
        buttonPressColor = darkGrey;
        buttonBorderColor = white;
    }
}
