package frc.virtualrobot.robot;

import frc.virtualrobot.robot.path.Environment;
import frc.virtualrobot.robot.path.Point;
import frc.virtualrobot.robot.path.RTRRTStar;

import java.util.List;

public class VirtualRobot {
    private Motor left;
    private Motor right;

    private Scheduler scheduler;
    private RobotLocationPredictor predictor;
    private VisualizerCommunicator visualizer;
    private RTRRTStar pathfinder;

    public VirtualRobot() {
        left = new Motor();
        right = new Motor();

        scheduler = new Scheduler();
        predictor = new RobotLocationPredictor(28.5, 3.75);
        visualizer = new VisualizerCommunicator();
        pathfinder = new RTRRTStar(new Environment(-200, -200, 200, 200), new Point(0, 0), new Point(125, 125), 1, 10, 10, 50, 0.1, 2, 10);

        // Send the tree to the visualizer every 20 ticks
        scheduler.scheduleRepeating(20, () -> {
            //visualizer.setPathTree(pathfinder.tree);
        });
    }

    public void update(double tps) {
        double leftTicks = left.getEncoderTicks();
        double rightTicks = right.getEncoderTicks();

        double leftRot = leftTicks / Motor.TICKS_PER_ROTATION * Math.PI * 2;
        double rightRot = rightTicks / Motor.TICKS_PER_ROTATION * Math.PI * 2;

        predictor.updateRotations(leftRot, rightRot);

        visualizer.setPredictedX(predictor.getX());
        visualizer.setPredictedY(predictor.getY());
        visualizer.setPredictedAngle(-predictor.getRotation()); // Invert rotation because visualizer expects counterclockwise rotation

        // Run the pathfinder and move towards the first point
        List<Point> path = pathfinder.runForTime(10);
        visualizer.setPath(path);
        if (path.isEmpty()) {
            double tx = Math.random() * 400 - 200;
            double ty = Math.random() * 400 - 200;
            pathfinder.setGoalPos(tx, ty);
            System.out.println("Reached the goal!");
        } else {
            Point target = path.get(0);
            double dx = target.getX() - predictor.getX();
            double dy = target.getY() - predictor.getY();
            double scale = Math.sqrt(dx * dx + dy * dy) * 2;
            dx /= scale;
            dy /= scale;

            double x = predictor.getX();
            double y = predictor.getY();
            x += dx;
            y += dy;
            predictor.setPosition(x, y);
            pathfinder.setAgentPos(x, y);
        }

        scheduler.tick();
        visualizer.setMemUsage();
        visualizer.setTPS(tps);
    }

    public void tick(double delta, double tps) {
        left.update(delta);
        right.update(delta);
        update(tps);
    }

    public Motor getLeft() {
        return left;
    }

    public Motor getRight() {
        return right;
    }
}
