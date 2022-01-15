package frc.shufflewood.gui.draw;

import frc.shufflewood.gui.Vec2;
import processing.core.PImage;

import java.util.ArrayList;
import java.util.List;

public final class GuiDrawData {
    public static final class Vertex {
        public Vec2 pos;
        public Vec2 uv;
        public int tint;
        public PImage texture;

        public Vertex(Vec2 pos, Vec2 uv, int tint, PImage texture) {
            this.pos = pos;
            this.uv = uv;
            this.tint = tint;
            this.texture = texture;
        }

        public Vec2 getPos() {
            return pos;
        }

        public void setPos(Vec2 pos) {
            this.pos = pos;
        }

        public Vec2 getUv() {
            return uv;
        }

        public void setUv(Vec2 uv) {
            this.uv = uv;
        }

        public int getTint() {
            return tint;
        }

        public void setTint(int tint) {
            this.tint = tint;
        }

        public PImage getTexture() {
            return texture;
        }

        public void setTexture(PImage texture) {
            this.texture = texture;
        }
    }

    private final List<Vertex> vertices;
    private final Vec2 whiteUV;
    private final PImage whiteTexture;

    public GuiDrawData(Vec2 whiteUV, PImage whiteTexture) {
        this.whiteUV = whiteUV;
        this.whiteTexture = whiteTexture;
        vertices = new ArrayList<>();
    }

    public void vertex(Vec2 pos, int color) {
        vertices.add(new Vertex(pos, whiteUV, color, whiteTexture));
    }

    public void texVertex(Vec2 pos, Vec2 uv, PImage texture, int tint) {
        vertices.add(new Vertex(pos, uv, tint, texture));
    }

    public List<Vertex> getVertices() {
        return vertices;
    }
}
