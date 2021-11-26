package frc.pathfinding.lib;

public class Grid {
    private final int width;
    private final int height;
    private final boolean[][] blocked;

    public Grid(int width, int height) {
        this.width = width;
        this.height = height;

        blocked = new boolean[width][height];
    }

    public boolean isCellBlocked(int x, int y) {
        return blocked[x][y];
    }

    public void setCellBlocked(int x, int y, boolean blocked) {
        this.blocked[x][y] = blocked;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
