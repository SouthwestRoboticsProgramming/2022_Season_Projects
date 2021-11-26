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

    private double heuristic(Node goal, Node cell) {
        return distance(goal, cell);
    }

    private List<Cell> reconstructPath(Node current) {
        List<Cell> path = new ArrayList<>();
        path.add(new Cell(current.x, current.y));
        while (current.parent != null) {
            current = current.parent;
            path.add(0, new Cell(current.x, current.y));
        }
        return path;
    }

    private boolean checkValidNeighbor(int x, int y) {
        return x >= 0 && x < grid.getWidth() &&
                y >= 0 && y < grid.getHeight() &&
                !grid.isCellBlocked(x, y);
    }

    private Set<Node> getNeighbors(Node[][] nodes, Node cell) {
        int x = cell.x;
        int y = cell.y;
        Set<Node> neighbors = new HashSet<>();

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0)
                    continue;

                if (checkValidNeighbor(x + i, y + j)) {
                    neighbors.add(getNode(nodes, x + i, y + j));
                }
            }
        }

        return neighbors;
    }

    private double distance(Node a, Node b) {
        double dx = a.x - b.x;
        double dy = a.y - b.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    private Node getNode(Node[][] nodes, int x, int y) {
        Node node = nodes[x][y];
        if (node == null) {
            node = new Node(x, y);
            nodes[x][y] = node;
        }
        return node;
    }

    public List<Cell> getPath() {
        // Make sure start and goal are set
        if (start == null) {
            throw new IllegalStateException("Start position has not been set");
        }
        if (goal == null) {
            throw new IllegalStateException("Goal position has not been set");
        }

        Node[][] nodes = new Node[grid.getWidth()][grid.getHeight()];
        Node startNode = getNode(nodes, start.getX(), start.getY());
        Node goalNode = getNode(nodes, goal.getX(), goal.getY());
        startNode.gScore = 0;
        startNode.fScore = heuristic(goalNode, startNode);

        Set<Node> openSet = new HashSet<>();
        openSet.add(startNode);

        while (!openSet.isEmpty()) {
            Node current = null;
            double lowestFScore = Double.POSITIVE_INFINITY;
            for (Node node : openSet) {
                double score = node.fScore;
                if (score < lowestFScore) {
                    current = node;
                    lowestFScore = score;
                }
            }
            if (current == null) {
                throw new IllegalStateException("Current was null!");
            }
            if (current.x == goal.getX() && current.y == goal.getY()) {
                return reconstructPath(current);
            }

            openSet.remove(current);
            for (Node neighbor : getNeighbors(nodes, current)) {
                double tentativeGScore = current.gScore + distance(current, neighbor);
                if (tentativeGScore < neighbor.gScore) {
                    neighbor.parent = current;
                    neighbor.gScore = tentativeGScore;
                    neighbor.fScore = neighbor.gScore + heuristic(goalNode, neighbor);
                    if (!openSet.contains(neighbor)) {
                        openSet.add(neighbor);
                    }
                }
            }
        }

        // No path was found
        return null;
    }

    private static class Node {
        private final int x;
        private final int y;
        private double fScore = Double.POSITIVE_INFINITY;
        private double gScore = Double.POSITIVE_INFINITY;
        private Node parent;

        public Node(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    public void setStartCell(Cell start) {
        this.start = start;
    }

    public void setGoalCell(Cell goal) {
        this.goal = goal;
    }
}
