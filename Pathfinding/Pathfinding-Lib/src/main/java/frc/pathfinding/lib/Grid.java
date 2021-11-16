package frc.pathfinding.lib;

public class Grid {
    private final int width;
    private final int height;
    private final boolean[][] environment;
    private final boolean[][] overlay;

    public Grid(int width, int height) {
        this.width = width;
        this.height = height;

        environment = new boolean[width][height];
        overlay = new boolean[width][height];
    }

    public boolean isCellBlocked(int x, int y) {
        return environment[x][y] || overlay[x][y];
    }

    public void setCellBlocked(int x, int y, boolean blocked) {
        overlay[x][y] = blocked;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
