package frc.pathfinding.lib.collision;

import frc.pathfinding.lib.Grid;

import java.util.HashSet;
import java.util.Set;

public class CollisionGrid extends Grid {
    private final Set<Collider> obstacles;
    private final Collider agent;
    private final double offsetX;
    private final double offsetY;

    public CollisionGrid(int width, int height, Collider agent) {
        super(width, height);
        obstacles = new HashSet<>();
        this.agent = agent;
        offsetX = agent.getX();
        offsetY = agent.getY();
    }

    @Override
    public boolean isCellBlocked(int x, int y) {
        return super.isCellBlocked(x, y) || checkCollision(x, y);
    }

    public void addObstacle(Collider c) {
        obstacles.add(c);
    }

    public void removeObstacle(Collider c) {
        obstacles.remove(c);
    }

    public Set<Collider> getObstacles() {
        return new HashSet<>(obstacles);
    }

    private boolean checkCollision(double x, double y) {
        agent.setX(x + offsetX + 0.5);
        agent.setY(y + offsetY + 0.5);

        for (Collider c : obstacles) {
            if (agent.collidesWith(c)) {
                return true;
            }
        }

        return false;
    }
}
