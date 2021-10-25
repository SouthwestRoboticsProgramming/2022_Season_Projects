package frc.visualizer;

import processing.core.PGraphics;

public class Grid {
    private final int cells;
    private final int cellSize;

    public Grid(int cells, int cellSize) {
        this.cells = cells;
        this.cellSize = cellSize;
    }

    public void draw(PGraphics g) {
        g.strokeWeight(1/5f);
        g.stroke(64);

        for (int x = -cells; x <= cells; x++) {
            g.line(x * cellSize, 0, -cells * cellSize, x * cellSize, 0, cells * cellSize);
        }
        for (int y = -cells; y <= cells; y++) {
            g.line(-cells * cellSize, 0, y * cellSize, cells * cellSize, 0, y * cellSize);
        }
    }
}
