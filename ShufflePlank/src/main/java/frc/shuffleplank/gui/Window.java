package frc.shuffleplank.gui;

import frc.shuffleplank.ShufflePlank;
import frc.shuffleplank.gui.draw.DrawList;

public class Window {
    public float x;
    public float y;
    public float w;
    public float h;
    public String name;
    public DrawList draw;

    public Window(String name) {
        this.name = name;
        x = ShufflePlank.instance.random(0, ShufflePlank.instance.width);
        y = ShufflePlank.instance.random(10, ShufflePlank.instance.height);
        w = 200;
        h = 250;
    }
}
