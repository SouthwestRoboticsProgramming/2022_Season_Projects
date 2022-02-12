package frc.shufflewood.gui.draw;

import frc.shufflewood.gui.Rect;
import frc.shufflewood.gui.Vec2;
import processing.core.PImage;

import java.util.ArrayDeque;
import java.util.Deque;

public class GuiDrawBuilder {
    private static final int CURVE_DETAIL = 8;
    private static final float[] curveLookup = new float[CURVE_DETAIL * 2 + 2];

    static {
        for (int i = 0; i <= CURVE_DETAIL; i++) {
            double angle = i / (double) CURVE_DETAIL * Math.PI / 2;

            curveLookup[i * 2] = (float) Math.cos(angle);
            curveLookup[i * 2 + 1] = (float) Math.sin(angle);
        }
    }

    private final Font font;
    private final GuiDrawData draw;
    private final Deque<Rect> clipStack;
    private Rect clipRect;

    public GuiDrawBuilder(Font font, int width, int height) {
        this.font = font;
        draw = new GuiDrawData(font.getWhiteUV(), font.getTexture());
        clipStack = new ArrayDeque<>();
        clipRect = new Rect(0, 0, width, height);
    }

    public GuiDrawData getDrawData() {
        return draw;
    }

    // --- Convex Polygon Triangulation ---

    private Vec2 firstPos, lastPos;
    private Vec2 firstUV, lastUV;
    private boolean firstPosSet, lastPosSet;
    private int polygonColor;

    public void beginPolygonBorder(int color) {
        firstPosSet = false;
        polygonColor = color;
    }

    public void polygonBorderVertex(Vec2 p) {
        if (!firstPosSet) {
            firstPos = p;
            lastPos = p;
            firstPosSet = true;
            return;
        }

        drawLine(lastPos, p, polygonColor);
        lastPos = p;
    }

    public void endPolygonBorder(GuiDrawData draw) {
        drawLine(lastPos, firstPos, polygonColor);
    }

    public void beginPolygon(int color) {
        polygonColor = color;
        firstPosSet = false;
        lastPosSet = false;
    }

    public void polygonVertex(Vec2 p) {
        polygonTextureVertex(p, font.getWhiteUV(), font.getTexture());
    }

    public void polygonTextureVertex(Vec2 p, Vec2 uv, PImage texture) {
        if (!firstPosSet) {
            firstPos = p;
            firstUV = uv;
            firstPosSet = true;
            return;
        } else if (!lastPosSet) {
            lastPos = p;
            lastUV = uv;
            lastPosSet = true;
            return;
        }

        draw.texVertex(firstPos, firstUV, texture, polygonColor);
        draw.texVertex(lastPos, lastUV, texture, polygonColor);
        draw.texVertex(p, uv, texture, polygonColor);
        lastPos = p;
        lastUV = uv;
    }

    // --- Draw Command Implementations ---

    public void drawLine(Vec2 a, Vec2 b, int color) {
        if (a.equals(b)) return; // No point in drawing a line with zero length

        float x1 = a.x;
        float y1 = a.y;
        float x2 = b.x;
        float y2 = b.y;

        final float halfSqrt2 = (float) Math.sqrt(2) / 2;

        // Get direction vector from point 2 to point 1 scaled so the line is 1 pixel wide
        float dx = x2 - x1;
        float dy = y2 - y1;
        float length = (float) Math.sqrt(dx * dx + dy * dy);
        float vx = dx / length * halfSqrt2;
        float vy = dy / length * halfSqrt2;

        // Vertex 1
        float vx1 = vx * halfSqrt2 - vy * halfSqrt2 + x2;
        float vy1 = vx * halfSqrt2 + vy * halfSqrt2 + y2;
        Vec2 v1 = new Vec2(vx1, vy1);

        // Vertex 2
        float vx2 = vx * halfSqrt2 - vy * -halfSqrt2 + x2;
        float vy2 = vx * -halfSqrt2 + vy * halfSqrt2 + y2;
        Vec2 v2 = new Vec2(vx2, vy2);

        // Vertex 3
        float vx3 = vx * -halfSqrt2 - vy * halfSqrt2 + x1;
        float vy3 = vx * halfSqrt2 + vy * -halfSqrt2 + y1;
        Vec2 v3 = new Vec2(vx3, vy3);

        // Vertex 4
        float vx4 = vx * -halfSqrt2 - vy * -halfSqrt2 + x1;
        float vy4 = vx * -halfSqrt2 + vy * -halfSqrt2 + y1;
        Vec2 v4 = new Vec2(vx4, vy4);

        fillTriangle(v1, v2, v4, color);
        fillTriangle(v1, v4, v3, color);
    }

    public void fillTriangle(Vec2 a, Vec2 b, Vec2 c, int color) {
        draw.vertex(a, color);
        draw.vertex(b, color);
        draw.vertex(c, color);
    }

    public void drawRect(Rect r, int color) {
        beginPolygonBorder(color);
        polygonBorderVertex(r.min);
        polygonBorderVertex(new Vec2(r.max.x, r.min.y));
        polygonBorderVertex(r.max);
        polygonBorderVertex(new Vec2(r.min.x, r.max.y));
        endPolygonBorder(draw);
    }

    public void fillRect(Rect r, int color) {
        r = clipRect.clip(r);
        if (r == null) return;

        beginPolygon(color);
        polygonVertex(r.min);
        polygonVertex(new Vec2(r.max.x, r.min.y));
        polygonVertex(r.max);
        polygonVertex(new Vec2(r.min.x, r.max.y));
    }

    public void drawRoundRect(Rect r, float round, int color) {
        // Edges
        drawLine(new Vec2(r.min.x + round, r.min.y), new Vec2(r.max.x - round, r.min.y), color);
        drawLine(new Vec2(r.min.x, r.min.y + round), new Vec2(r.min.x, r.max.y - round), color);
        drawLine(new Vec2(r.min.x + round, r.max.y), new Vec2(r.max.x - round, r.max.y), color);
        drawLine(new Vec2(r.max.x, r.min.y + round), new Vec2(r.max.x, r.max.y - round), color);

        // Corners
        for (int i = 1; i <= CURVE_DETAIL; i++) {
            float lx = curveLookup[i * 2 - 2];
            float ly = curveLookup[i * 2 - 1];
            float px = curveLookup[i * 2];
            float py = curveLookup[i * 2 + 1];

            drawLine(new Vec2(r.max.x - round + round * lx, r.max.y - round + round * ly), new Vec2(r.max.x - round + round * px, r.max.y - round + round * py), color);
            drawLine(new Vec2(r.min.x + round - round * lx, r.min.y + round - round * ly), new Vec2(r.min.x + round - round * px, r.min.y + round - round * py), color);
            drawLine(new Vec2(r.max.x - round + round * lx, r.min.y + round - round * ly), new Vec2(r.max.x - round + round * px, r.min.y + round - round * py), color);
            drawLine(new Vec2(r.min.x + round - round * lx, r.max.y - round + round * ly), new Vec2(r.min.x + round - round * px, r.max.y - round + round * py), color);
        }
    }

    public void fillRoundRect(Rect r, float round, int color) {
        beginPolygon(color);

        // Bottom right
        for (int i = 0; i <= CURVE_DETAIL; i++) {
            float vx = curveLookup[i * 2] * round + r.max.x - round;
            float vy = curveLookup[i * 2 + 1] * round + r.max.y - round;
            polygonVertex(new Vec2(vx, vy));
        }

        // Bottom left
        for (int i = CURVE_DETAIL; i >= 0; i--) {
            float vx = r.min.x + round - curveLookup[i * 2] * round;
            float vy = curveLookup[i * 2 + 1] * round + r.max.y - round;
            polygonVertex(new Vec2(vx, vy));
        }

        // Top left
        for (int i = 0; i <= CURVE_DETAIL; i++) {
            float vx = r.min.x + round - curveLookup[i * 2] * round;
            float vy = r.min.y + round - curveLookup[i * 2 + 1] * round;
            polygonVertex(new Vec2(vx, vy));
        }

        // Top right
        for (int i = CURVE_DETAIL; i >= 0; i--) {
            float vx = curveLookup[i * 2] * round + r.max.x - round;
            float vy = r.min.y + round - curveLookup[i * 2 + 1] * round;
            polygonVertex(new Vec2(vx, vy));
        }
    }

    public void textureRect(Rect r, Rect uv, PImage texture, int color) {
        Rect clipped = clipRect.clip(r);
        r = clipped;

        beginPolygon(color);
        polygonTextureVertex(r.min, uv.min, texture);
        polygonTextureVertex(new Vec2(r.max.x, r.min.y), new Vec2(uv.max.x, uv.min.y), texture);
        polygonTextureVertex(r.max, uv.max, texture);
        polygonTextureVertex(new Vec2(r.min.x, r.max.y), new Vec2(uv.min.x, uv.max.y), texture);
    }

    public void fillSector(Rect circleBounds, float minAngle, float maxAngle, int color) {
        beginPolygon(color);

        float centerX = circleBounds.min.x + circleBounds.getWidth() / 2;
        float centerY = circleBounds.min.y + circleBounds.getHeight() / 2;
        polygonVertex(new Vec2(centerX, centerY));

        float minX = (float) Math.cos(minAngle) * circleBounds.getWidth() / 2 + centerX;
        float minY = (float) Math.sin(minAngle) * circleBounds.getHeight() / 2 + centerY;
        polygonVertex(new Vec2(minX, minY));

        // TODO: walk around the arc

        float maxX = (float) Math.cos(maxAngle) * circleBounds.getWidth() / 2 + centerX;
        float maxY = (float) Math.sin(maxAngle) * circleBounds.getHeight() / 2 + centerY;
        polygonVertex(new Vec2(maxX, maxY));

        return;
    }

    private float map(float val, float min, float max, float newMin, float newMax) {
        float l = (val - min) / (max - min);
        return l * (newMax - newMin) + newMin;
    }

    public void setClipRect(Rect r) {
        clipStack.clear();
        clipRect = r;
    }

    public void pushClipRect(Rect r) {
        clipStack.push(clipRect);
        clipRect = clipRect.clip(r);
    }

    public void popClipRect() {
        clipRect = clipStack.pop();
    }
}
