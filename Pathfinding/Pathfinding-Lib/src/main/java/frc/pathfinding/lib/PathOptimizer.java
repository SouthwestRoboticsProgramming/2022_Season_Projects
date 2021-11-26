package frc.pathfinding.lib;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PathOptimizer {
    private final Grid grid;

    public PathOptimizer(Grid grid) {
        this.grid = grid;
    }

    private boolean testLineOfSight(Cell start, Cell end) {
        // TODO: Use a proper algorithm

        double precision = 0.01;
        for (int i = 0; i <= 100; i++) {
            double x = start.getX() + (end.getX() - start.getX()) * precision * i + 0.5;
            double y = start.getY() + (end.getY() - start.getY()) * precision * i + 0.5;

            if (grid.isCellBlocked((int) x, (int) y)) {
                return false;
            }
        }

        return true;
    }

    public List<Cell> optimize(List<Cell> path) {
        List<Cell> output = new ArrayList<>();

        for (int i = 0; i < path.size(); i++) {
            Cell cell = path.get(i);
            output.add(cell);

            while (i < path.size() - 2) {
                if (!testLineOfSight(cell, path.get(i + 2))) {
                    break;
                }
                i++;
            }
        }

        return output;
    }
}
