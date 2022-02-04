package frc.pathfinding.task;

import frc.messenger.client.MessengerClient;
import frc.pathfinding.lib.Cell;
import frc.pathfinding.lib.PathOptimizer;
import frc.pathfinding.lib.Pathfinder;
import frc.pathfinding.lib.collision.CircleCollider;
import frc.pathfinding.lib.collision.Collider;
import frc.pathfinding.lib.collision.CollisionGrid;
import frc.pathfinding.lib.collision.RectangleCollider;

import java.io.*;
import java.util.List;
import java.util.Properties;

public final class PathfinderTask {
    private static final String CONFIG_FILE = "config.properties";
    private static final String IN_SET_POSITION = "Pathfinder:SetPosition";
    private static final String IN_SET_TARGET = "Pathfinder:SetTarget";
    private static final String OUT_PATH = "Pathfinder:Path";

    private void run() {
        Properties config = new Properties();
        try {
            config.load(new FileReader(CONFIG_FILE));
        } catch (IOException e) {
            System.err.println("Failed to load configuration file, using default config");
            config.setProperty("host", "localhost");
            config.setProperty("port", "5805");
            config.setProperty("name", "Pathfinder");
            config.setProperty("agentRadius", "5.89");
            config.setProperty("collisionPadding", "1");
            try {
                config.store(new FileWriter(CONFIG_FILE), "Configuration for Pathfinder");
            } catch (IOException e2) {
                System.err.println("Failed to save default config file:");
                e2.printStackTrace();
            }
        }

        String host = config.getProperty("host");
        int port = Integer.parseInt(config.getProperty("port"));
        String name = config.getProperty("name");
        float agentRadius = Float.parseFloat(config.getProperty("agentRadius"));
        float collisionPadding = Float.parseFloat(config.getProperty("collisionPadding"));

        System.out.println("Connecting to Messenger server at " + host + ":" + port + " as " + name);
        MessengerClient msg = new MessengerClient(host, port, name);
        System.out.println("Connected");

        Collider robotCollider = new CircleCollider(0, 0, agentRadius + collisionPadding);

        CollisionGrid grid = new CollisionGrid(30, 30, robotCollider);
        grid.loadFromFile("scene.txt");
        Pathfinder pathfinder = new Pathfinder(grid);
        PathOptimizer optimizer = new PathOptimizer(grid);
        // Default start and goal so it doesn't crash
        pathfinder.setStartCell(new Cell(0, 0));
        pathfinder.setGoalCell(new Cell(0, 0));

        msg.listen(IN_SET_TARGET);
        msg.setCallback((type, data) -> {
            DataInputStream in = new DataInputStream(new ByteArrayInputStream(data));
            try {
                switch (type) {
                    case IN_SET_TARGET: {
                        int x = in.readInt();
                        int y = in.readInt();
                        pathfinder.setGoalCell(new Cell(x, y));
                        System.out.println("Now targeting cell " + x + ", " + y);
                    }
                    case IN_SET_POSITION: {
                        int x = in.readInt();
                        int y = in.readInt();
                        pathfinder.setStartCell(new Cell(x, y));
                        // Don't print, this will be called frequently
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        while (true) {
            msg.read();

            // Calculate path
            List<Cell> path = pathfinder.getPath();
            path = optimizer.optimize(path);

            // Send path data
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream d = new DataOutputStream(b);
            try {
                d.writeInt(path.size());
                for (Cell cell : path) {
                    d.writeInt(cell.getX());
                    d.writeInt(cell.getY());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            msg.sendMessage(OUT_PATH, b.toByteArray());

            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                // Ignore
            }
        }
    }

    public static void main(final String[] args) {
        new PathfinderTask().run();
    }
}
