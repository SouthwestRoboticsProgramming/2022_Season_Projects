package frc.shufflewood;

import java.util.ArrayList;
import java.util.List;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;

public final class DrawList {
    private static final boolean DEBUG_WIREFRAME = false;
    
    private static class Vertex {
        float x, y;
        float u, v;
        int color;
        PImage texture;
        
        private Vertex(float x, float y, float u, float v, int color, PImage texture) {
            this.x = x;
            this.y = y;
            this.u = u;
            this.v = v;
            this.color = color;
            this.texture = texture;
        }
    }

    private static final int CURVE_DETAIL = 8;
    private static final float[] curveLookup = new float[CURVE_DETAIL * 2 + 2];
    static {
        for (int i = 0; i <= CURVE_DETAIL; i++) {
            double angle = i / (double) CURVE_DETAIL * Math.PI / 2;

            curveLookup[i * 2    ] = (float) Math.cos(angle);
            curveLookup[i * 2 + 1] = (float) Math.sin(angle);
        }
    }
    
    private final List<Vertex> vertices;
    private final PApplet app;
    private final Font font;
    
    public DrawList(PApplet app, Font font) {
        vertices = new ArrayList<>();
        this.app = app;
        this.font = font;
    }
    
    public void drawLine(float x1, float y1, float x2, float y2, int color) {
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

        // Vertex 2
        float vx2 = vx * halfSqrt2 - vy * -halfSqrt2 + x2;
        float vy2 = vx * -halfSqrt2 + vy * halfSqrt2 + y2;

        // Vertex 3
        float vx3 = vx * -halfSqrt2 - vy * halfSqrt2 + x1;
        float vy3 = vx * halfSqrt2 + vy * -halfSqrt2 + y1;

        // Vertex 4
        float vx4 = vx * -halfSqrt2 - vy * -halfSqrt2 + x1;
        float vy4 = vx * -halfSqrt2 + vy * -halfSqrt2 + y1;

        fillTriangle(vx1, vy1, vx2, vy2, vx4, vy4, color);
        fillTriangle(vx1, vy1, vx4, vy4, vx3, vy3, color);
    }
    
    public void drawRect(float x, float y, float w, float h, int color) {
        drawLine(x, y, x + w, y, color);
        drawLine(x, y, x, y + h, color);
        drawLine(x + w, y, x + w, y + h, color);
        drawLine(x, y + h, x + w, y + h, color);
    }
    
    public void fillRect(float x, float y, float w, float h, int color) {
        fillTriangle(x, y, x + w, y, x + w, y + h, color);
        fillTriangle(x, y, x + w, y + h, x, y + h, color);
    }
    
    public void drawText(String text, float x, float y, int color) { drawText(text, x, y, color, 0f); }
    public void drawText(String text, float x, float y, int color, float v) { drawText(text, x, y, color, v, 0f); }
    public void drawText(String text, float x, float y, int color, float v, float h) {
        y -= font.getHeight() * v;
        x -= font.getWidth(text) * h;
        
        font.draw(this, text, x, y, color);
    }

    public void drawRoundRect(float x, float y, float w, float h, float round, int color) {
        // Edges
        drawLine(x + round, y, x + w - round, y, color);
        drawLine(x, y + round, x, y + h - round, color);
        drawLine(x + round, y + h, x + w - round, y + h, color);
        drawLine(x + w, y + round, x + w, y + h - round, color);

        // Corners
        for (int i = 1; i <= CURVE_DETAIL; i++) {
            float lx = curveLookup[i * 2 - 2];
            float ly = curveLookup[i * 2 - 1];
            float px = curveLookup[i * 2    ];
            float py = curveLookup[i * 2 + 1];

            drawLine(x + w - round + round * lx, y + h - round + round * ly, x + w - round + round * px, y + h - round + round * py, color);
            drawLine(x + round - round * lx, y + round - round * ly, x + round - round * px, y + round - round * py, color);
            drawLine(x + w - round + round * lx, y + round - round * ly, x + w - round + round * px, y + round - round * py, color);
            drawLine(x + round - round * lx, y + h - round + round * ly, x + round - round * px, y + h - round + round * py, color);
        }
    }

    float polyFirstX, polyFirstY;
    float polyLastX, polyLastY;
    boolean polyFirstSet, polyLastSet;
    int polyColor;
    private void beginPolygon(int color) {
        polyFirstSet = false;
        polyLastSet = false;
        polyColor = color;
    }

    private void polygonVtx(float x, float y) {
        if (!polyFirstSet) {
            polyFirstX = x;
            polyFirstY = y;
            polyFirstSet = true;
            return;
        }
        if (!polyLastSet) {
            polyLastX = x;
            polyLastY = y;
            polyLastSet = true;
            return;
        }

        colorVtx(polyFirstX, polyFirstY, polyColor);
        colorVtx(polyLastX, polyLastY, polyColor);
        colorVtx(x, y, polyColor);

        polyLastX = x;
        polyLastY = y;
    }

    public void fillRoundRect(float x, float y, float w, float h, float round, int color) {
        float fx = x;
        float fy = y + round;

        beginPolygon(color);

        // Bottom right
        for (int i = 0; i <= CURVE_DETAIL; i++) {
            float vx = curveLookup[i * 2] * round + x + w - round;
            float vy = curveLookup[i*2+1] * round + y + h - round;
            polygonVtx(vx, vy);
        }

        // Bottom left
        for (int i = CURVE_DETAIL; i >= 0; i--) {
            float vx = x + round - curveLookup[i * 2] * round;
            float vy = curveLookup[i*2+1] * round + y + h - round;
            polygonVtx(vx, vy);
        }

        // Top left
        for (int i = 0; i <= CURVE_DETAIL; i++) {
            float vx = x + round - curveLookup[i * 2] * round;
            float vy = y + round - curveLookup[i*2+1] * round;
            polygonVtx(vx, vy);
        }

        // Top right
        for (int i = CURVE_DETAIL; i >= 0; i--) {
            float vx = curveLookup[i * 2] * round + x + w - round;
            float vy = y + round - curveLookup[i * 2 + 1] * round;
            polygonVtx(vx, vy);
        }
    }
    
    public void fillTriangle(float x1, float y1, float x2, float y2, float x3, float y3, int color) {
        colorVtx(x1, y1, color);
        colorVtx(x2, y2, color);
        colorVtx(x3, y3, color);
    }
    
    public void fillTexturedRect(float x, float y, float w, float h, int u1, int v1, int u2, int v2, PImage texture, int tint) {
        // Triangle 1
        textureVtx(x,     y,     u1, v1, tint, texture);
        textureVtx(x + w, y,     u2, v1, tint, texture);
        textureVtx(x + w, y + h, u2, v2, tint, texture);

        // Triangle 2
        textureVtx(x,     y,     u1, v1, tint, texture);
        textureVtx(x + w, y + h, u2, v2, tint, texture);
        textureVtx(x,     y + h, u1, v2, tint, texture);
    }
    
    public void append(DrawList other) {
        vertices.addAll(other.vertices);
    }
    
    public void draw() {
        app.noStroke();

        if (DEBUG_WIREFRAME) {
            app.stroke(255);
            app.noFill();
        }

        PImage texture = vertices.get(0).texture;
        app.beginShape(PConstants.TRIANGLES);
        for (Vertex v : vertices) {
            if (v.texture != texture) {
                app.endShape();
                app.beginShape(PConstants.TRIANGLES);
                texture = v.texture;
            }
            if (!DEBUG_WIREFRAME) {
              app.texture(v.texture);
              app.tint(v.color);
            }
            app.vertex(v.x, v.y, v.u, v.v);
        }
        app.endShape();
    }
    
    private void colorVtx(float x, float y, int color) {
        Vec2f whiteUV = font.getWhiteUV();
        vertices.add(new Vertex(x, y, whiteUV.x, whiteUV.y, color, font.getImage()));
    }
    
    private void textureVtx(float x, float y, float u, float v, int tint, PImage texture) {
        vertices.add(new Vertex(x, y, u, v, tint, texture));
    }
}
