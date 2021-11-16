package frc.pathfinding.lib;

import java.util.Objects;

public class Cell {
    private final int x;
    private final int y;

    public Cell(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    // WARNING: Assumes the parameter will always be a cell!
    @Override
    public boolean equals(Object o) {
        Cell cell = (Cell) o;
        return x == cell.x &&
                y == cell.y;
    }

    @Override
    public int hashCode() {
        return x * 31 * 31 + y;
    }
}
