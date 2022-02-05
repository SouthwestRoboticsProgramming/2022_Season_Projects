package frc.pathfinding.lib.collision;

import frc.pathfinding.lib.Grid;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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

    public void loadFromFile(String file) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(new File(file)));

            String line;
            while ((line = br.readLine()) != null) {
                if ("".equals(line)) continue;
                if (line.startsWith("#")) continue;

                String[] tokens = line.split(" ");
                switch (tokens[0]) {
                    case "circle": {
                        float x = 0, y = 0, radius = 0;
                        for (int i = 1; i < tokens.length; i++) {
                            String[] parts = tokens[i].split("=");
                            float value = Float.parseFloat(parts[1]);
                            switch (parts[0]) {
                                case "x": x = value; break;
                                case "y": y = value; break;
                                case "radius": radius = value; break;
                            }
                        }
                        System.out.println("Circle collider at (" + x + ", " + y + ") with radius " + radius);
                        addObstacle(new CircleCollider(x, y, radius));
                    }
                    case "rectangle": {
                        float x = 0, y = 0, width = 0, height = 0, rotation = 0;
                        for (int i = 1; i < tokens.length; i++) {
                            String[] parts = tokens[i].split("=");
                            float value = Float.parseFloat(parts[1]);
                            switch (parts[0]) {
                                case "x": x = value; break;
                                case "y": y = value; break;
                                case "width": width = value; break;
                                case "height": height = value; break;
                                case "rotation": rotation = (float) Math.toRadians(value); break;
                            }
                        }
                        System.out.println("Rectangle collider at (" + x + ", " + y + ") with size (" + width + ", " + height + ") with rotation " + rotation);
                        addObstacle(new RectangleCollider(x, y, width, height, rotation));
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to load scene!");
            e.printStackTrace();
            System.exit(1);
        }
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
