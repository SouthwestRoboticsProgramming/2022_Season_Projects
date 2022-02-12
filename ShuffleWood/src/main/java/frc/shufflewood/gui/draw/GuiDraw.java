package frc.shufflewood.gui.draw;

import frc.shufflewood.gui.Rect;
import frc.shufflewood.gui.Vec2;
import processing.core.PImage;

import java.util.ArrayList;
import java.util.List;

public final class GuiDraw {
    private final List<GuiDrawCmd> cmds;
    private Font font;
    private int width;
    private int height;

    public GuiDraw() {
        cmds = new ArrayList<>();
    }

    public void drawLine(Vec2 a, Vec2 b, int color)                     { cmds.add((draw) -> draw.drawLine     (new Vec2(a), new Vec2(b), color));   }
    public void fillTriangle(Vec2 a, Vec2 b, Vec2 c, int color)         { cmds.add((draw) -> draw.fillTriangle (new Vec2(a), new Vec2(b), new Vec2(c), color)); }
    public void drawRect(Rect r, int color)                             { cmds.add((draw) -> draw.drawRect     (new Rect(r), color));                }
    public void fillRect(Rect r, int color)                             { cmds.add((draw) -> draw.fillRect     (new Rect(r), color));                }
    public void drawRoundRect(Rect r, float round, int color)           { cmds.add((draw) -> draw.drawRoundRect(new Rect(r), round, color));         }
    public void fillRoundRect(Rect r, float round, int color)           { cmds.add((draw) -> draw.fillRoundRect(new Rect(r), round, color));         }
    public void drawText(String text, Vec2 pos, int color)              { drawText(text, new Vec2(pos), color, new Vec2(0, 0));                }
    public void drawText(String text, Vec2 pos, int color, Vec2 align)  {
        float y = pos.y - font.getHeight() * align.y;
        float x = pos.x - font.getWidth(text) * align.x;

        font.draw(this, text, x, y, color);
    }
    public void fillSector(Rect circleBounds, float startAngle, float stopAngle, int color) { cmds.add((draw) -> draw.fillSector(new Rect(circleBounds), startAngle, stopAngle, color)); }
    public void textureRect(Rect r, Rect uv, PImage texture, int color) { cmds.add((draw) -> draw.textureRect  (new Rect(r), new Rect(uv), texture, color));   }
    public void setClipRect(Rect r)                                     { cmds.add((draw) -> draw.setClipRect  (new Rect(r)));                       }
    public void pushClipRect(Rect r)                                    { cmds.add((draw) -> draw.pushClipRect (new Rect(r)));                       }
    public void popClipRect()                                           { cmds.add((draw) -> draw.popClipRect  ());                                  }

    public void append(GuiDraw draw) { cmds.addAll(draw.cmds); }
    public GuiDraw split() {
        GuiDraw draw = new GuiDraw();
        draw.font = font;
        draw.width = width;
        draw.height = height;
        return draw;
    }

    public void setFont(Font font) {
        this.font = font;
    }

    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void reset() {
        cmds.clear();
    }

    public GuiDrawData buildDrawData() {
        GuiDrawBuilder draw = new GuiDrawBuilder(font, width, height);

        for (GuiDrawCmd cmd : cmds) {
            cmd.draw(draw);
        }

        return draw.getDrawData();
    }

    @FunctionalInterface
    private interface GuiDrawCmd {
        void draw(GuiDrawBuilder draw);
    }
}