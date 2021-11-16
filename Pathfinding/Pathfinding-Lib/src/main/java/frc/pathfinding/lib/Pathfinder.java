package frc.pathfinding.lib;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Pathfinder {
    private final Grid grid;
    private Cell start = null;
    private Cell goal = null;

    public Pathfinder(Grid grid) {
        this.grid = grid;
    }

    private double heuristic(Cell cell) {
        return distance(start, cell);
    }

    private List<Cell> reconstructPath(Map<Cell, Cell> cameFrom, Cell current) {
        List<Cell> path = new ArrayList<>();
        path.add(current);
        while (cameFrom.containsKey(current)) {
            current = cameFrom.get(current);
            path.add(0, current);
        }
        return path;
    }

    private boolean checkValidNeighbor(int x, int y) {
        return x >= 0 && x < grid.getWidth() &&
                y >= 0 && y < grid.getHeight() &&
                !grid.isCellBlocked(x, y);
    }

    private Set<Cell> getNeighbors(Cell cell) {
        int x = cell.getX();
        int y = cell.getY();
        Set<Cell> neighbors = new HashSet<>();

        if (checkValidNeighbor(x - 1, y))
            neighbors.add(new Cell(x - 1, y));
        if (checkValidNeighbor(x + 1, y))
            neighbors.add(new Cell(x + 1, y));
        if (checkValidNeighbor(x, y - 1))
            neighbors.add(new Cell(x, y - 1));
        if (checkValidNeighbor(x, y + 1))
            neighbors.add(new Cell(x, y + 1));

        return neighbors;
    }

    private double distance(Cell a, Cell b) {
        double dx = a.getX() - b.getX();
        double dy = a.getY() - b.getY();
        return Math.sqrt(dx * dx + dy * dy);
    }

    public List<Cell> getPath() {
        // Make sure start and goal are set
        if (start == null) {
            throw new IllegalStateException("Start position has not been set");
        }
        if (goal == null) {
            throw new IllegalStateException("Goal position has not been set");
        }

        Set<Cell> openSet = new HashSet<>();
        openSet.add(start);
        Map<Cell, Cell> cameFrom = new HashMap<>();
        Map<Cell, Double> gScore = new HashMap<>();
        gScore.put(start, 0.0);
        Map<Cell, Double> fScore = new HashMap<>();
        fScore.put(start, heuristic(start));

        while (!openSet.isEmpty()) {
            Cell current = null;
            double lowestFScore = Double.POSITIVE_INFINITY;
            for (Cell cell : openSet) {
                double score = fScore.getOrDefault(cell, Double.POSITIVE_INFINITY);
                if (score < lowestFScore) {
                    current = cell;
                    lowestFScore = score;
                }
            }
            if (current == null) {
                throw new IllegalStateException("Current was null!");
            }
            if (current.equals(goal)) {
                return reconstructPath(cameFrom, current);
            }

            openSet.remove(current);
            for (Cell neighbor : getNeighbors(current)) {
                double tentativeGScore = gScore.get(current) + distance(current, neighbor);
                if (tentativeGScore < gScore.getOrDefault(neighbor, Double.POSITIVE_INFINITY)) {
                    cameFrom.put(neighbor, current);
                    gScore.put(neighbor, tentativeGScore);
                    fScore.put(neighbor, tentativeGScore + heuristic(neighbor));
                    if (!openSet.contains(neighbor)) {
                        openSet.add(neighbor);
                    }
                }
            }
        }

        // No path was found
        return null;
    }

    public void setStartCell(Cell start) {
        this.start = start;
    }

    public void setGoalCell(Cell goal) {
        this.goal = goal;
    }
}
