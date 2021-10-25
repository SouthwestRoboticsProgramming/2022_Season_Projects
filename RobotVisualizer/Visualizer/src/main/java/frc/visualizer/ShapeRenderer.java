package frc.visualizer;

import processing.core.PGraphics;

public final class ShapeRenderer {
    public static void cylinder(PGraphics g, float radius, float height) {
        cylinder(g, radius, height, 24);
    }

    public static void cylinder(PGraphics g, float radius, float height, int detail) {
        height /= 2;

        // Draw edge
        g.beginShape(g.TRIANGLE_STRIP);
        double angleStep = Math.PI * 2 / detail;
        for (int i = 0; i <= detail; i++) {
            double angle = angleStep * i;

            float sin = (float) Math.sin(angle);
            float cos = (float) Math.cos(angle);
            g.vertex(cos * radius, height, sin * radius);
            g.vertex(cos * radius, -height, sin * radius);
        }
        g.endShape();

        // Draw top
        g.beginShape(g.TRIANGLE_FAN);
        g.vertex(0, height, 0);
        for (int i = 0; i <= detail; i++) {
            double angle = angleStep * i;
            g.vertex((float) Math.cos(angle) * radius, height, (float) Math.sin(angle) * radius);
        }
        g.endShape();

        // Draw bottom
        g.beginShape(g.TRIANGLE_FAN);
        g.vertex(0, -height, 0);
        for (int i = 0; i <= detail; i++) {
            double angle = angleStep * i;
            g.vertex((float) Math.cos(angle) * radius, -height, (float) Math.sin(angle) * radius);
        }
        g.endShape();
    }

    private ShapeRenderer() {
        throw new AssertionError();
    }
}
