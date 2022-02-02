package frc.shufflewood.gui.draw;

import frc.shufflewood.gui.Rect;
import frc.shufflewood.gui.Vec2;
import processing.core.PApplet;
import processing.core.PImage;

import java.util.HashMap;
import java.util.Map;

public final class Font {
    private static class CharInfo {
        // Region on texture atlas
        int x, y;
        int w, h;

        // Offset from baseline
        int offsetX, offsetY;

        // Amount to move on x axis
        int xAdvance;
    }

    private final PImage img;
    private final Map<Character, CharInfo> chars;
    private final float scale;

    private float verticalCorrection;
    private float height;

    public Font(PApplet app, float scale) {
        img = app.loadImage("fonts.png");
        this.scale = scale;

        chars = new HashMap<>();
        String[] data = app.loadStrings("PTSans-Regular-64.fnt");

        for (String line : data) {
            String[] tokens = line.split(" +");
            if (tokens.length == 0) continue;

            if (tokens[0].equals("char")) {
                CharInfo info = new CharInfo();
                char c = 0;
                for (int i = 1; i < tokens.length; i++) {
                    String[] parts = tokens[i].split("=");
                    int val = Integer.parseInt(parts[1]);
                    switch (parts[0]) {
                        case "id":       c      = (char) val; break;
                        case "x":        info.x        = val; break;
                        case "y":        info.y        = val; break;
                        case "width":    info.w        = val; break;
                        case "height":   info.h        = val; break;
                        case "xoffset":  info.offsetX  = val; break;
                        case "yoffset":  info.offsetY  = val; break;
                        case "xadvance": info.xAdvance = val; break;
                    }
                }

                chars.put(c, info);
            }
        }

        height = chars.get('W').h; // Just choose one
        verticalCorrection = 65 - height; // TODO: Don't hardcode
    }

    public void draw(GuiDraw draw, String text, float x, float y, int color) {
        //draw.fillRect(x, y, getWidth(text), getHeight(), 0xffff0000);
        y -= verticalCorrection * scale;
        for (char c : text.toCharArray()) {
            CharInfo info = chars.get(c);
            if (info == null) {
                info = chars.get('?');
            }

            Rect r = new Rect(
                    x + info.offsetX * scale, y + info.offsetY * scale,
                    x + info.offsetX * scale + info.w * scale, y + info.offsetY * scale + info.h * scale
            );
            Rect uv = new Rect(
                    info.x, info.y,
                    info.x + info.w, info.y + info.h
            );

            draw.textureRect(r, uv, img, color);

            x += info.xAdvance * scale;
        }
    }

    public float getHeight() {
        return height * scale;
    }

    public float getWidth(String text) {
        float x = 0;

        for (char c : text.toCharArray()) {
            CharInfo info = chars.get(c);
            if (info == null) {
                info = chars.get('?');
            }

            x += info.xAdvance * scale;
        }

        return x;
    }

    public PImage getTexture() {
        return img;
    }

    public Vec2 getWhiteUV() {
        return new Vec2(97, 161);
    }
}
