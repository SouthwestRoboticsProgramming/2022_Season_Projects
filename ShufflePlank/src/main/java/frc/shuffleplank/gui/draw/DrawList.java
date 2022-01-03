package frc.shuffleplank.gui.draw;

import frc.shuffleplank.ShufflePlank;
import processing.core.PApplet;

import java.util.ArrayList;
import java.util.List;

public class DrawList {
    public final List<DrawCommand> cmds;

    public DrawList() {
        cmds = new ArrayList<DrawCommand>();
    }

    public void drawLine(float x1, float y1, float x2, float y2, int col) { cmds.add(new DrawLineCommand(x1, y1, x2, y2, col)); }
    public void drawRect(float x, float y, float w, float h, int col) { cmds.add(new DrawRectCommand(x, y, w, h, col)); }
    public void fillRect(float x, float y, float w, float h, int col) { cmds.add(new FillRectCommand(x, y, w, h, col)); }
    public void setClip(float x, float y, float w, float h) { cmds.add(new SetClipCommand(x, y, w, h)); }
    public void noClip() { cmds.add(new SetClipCommand(0, 0, ShufflePlank.instance.width, ShufflePlank.instance.height)); }
    public void drawText(String str, float x, float y, int c) { cmds.add(new DrawTextCommand(str, x, y, c)); }
    public void add(DrawList other) { cmds.addAll(other.cmds); }

    public void draw(PApplet p) {
        for (DrawCommand cmd : cmds) {
            cmd.draw(p);
        }
    }
}
