package frc.lidar.demo;

import frc.messenger.client.MessengerClient;
import processing.core.PApplet;
import processing.event.MouseEvent;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.*;

public class NewDemo extends PApplet {
    @Override
    public void settings() {
        size(1440, 800, P2D);
    }

    // Settings
    private static final boolean DRAW_POINTS = true;
    private static final boolean DRAW_LINES = false;

    // Constants
    private static final float ROBOT_DIAMETER = 0.34f;
    private static final float LIDAR_OFFSET_X = 0;//-0.1016f; // These are estimates converted from inches to centimeters
    private static final float LIDAR_OFFSET_Y = 0;//-0.0381f;
    private static final int MAX_PAST_SAMPLES = 25;

    // Communication
    private MessengerClient msg;

    // Data storage
    private List<List<Sample>> pastSamples;
    private Set<Sample> incomingSamples;
    private Set<Sample> lidarSamples;
    private float robotX = 0, robotY = 0;
    private float robotAngle = 0;
    private float lidarX = LIDAR_OFFSET_X, lidarY = LIDAR_OFFSET_Y;

    // View parameters
    private float zoom = 200f;
    private float panX = 0, panY = 0;

    @Override
    public void setup() {
        ellipseMode(CENTER);

        pastSamples = new ArrayList<>();
        incomingSamples = new HashSet<>();
        lidarSamples = new HashSet<>();
        lidarSamples.add(new Sample(1, 0));
        lidarSamples.add(new Sample(0, 1));

        msg = new MessengerClient("10.21.29.3", 5805, "LidarDemo");
        msg.setCallback(this::messageCallback);
        msg.listen("Lidar:Scan");
        msg.listen("Lidar:ScanStart");
        msg.listen("RoboRIO:Location");
    }

    @Override
    public void draw() {
        // Read in messages
        msg.read();

        // Set up the scene
        background(0);
        translate(width / 2f, height / 2f);
        scale(zoom, -zoom);
        translate(panX, -panY);

        // Draw past samples
        for (int i = 0; i < pastSamples.size(); i++) {
            List<Sample> samples = pastSamples.get(i);

            // Make older samples darker red
            int color = color(map(i, 0, pastSamples.size(), 64, 255), 0, 0);

            drawSampleSet(samples, color);
        }

        // Sort the incoming samples by angle around the lidar so the line works right
        List<Sample> sortedSamples = sortSamples(lidarSamples);

        // Draw the incoming samples
        drawSampleSet(sortedSamples, color(255, 0, 0));

        // Move the origin to the robot position
        translate(robotX, robotY);

        // Draw the bounding circle of the robot
        /*stroke(255);
        strokeWeight(1);
        fill(128, 64);
        ellipse(0, 0, ROBOT_DIAMETER, ROBOT_DIAMETER);

        // Draw an arrow indicating the direction the robot is facing
        stroke(255);
        strokeWeight(1);
        pushMatrix();
        {
            rotate(robotAngle);
            float quarterDiameter = ROBOT_DIAMETER / 4f;
            line(-quarterDiameter, 0, quarterDiameter, 0);
            line(0, quarterDiameter, quarterDiameter, 0);
            line(0, -quarterDiameter, quarterDiameter, 0);
        }
        popMatrix();

        // Indicate the position of the lidar on the robot
        stroke(0, 0, 255);
        strokeWeight(6);
        point(lidarX, lidarY);*/
    }

    private List<Sample> sortSamples(Set<Sample> samples) {
        List<Sample> sortedSamples = new ArrayList<>(samples);
        sortedSamples.sort(Comparator.comparingDouble((sample) -> Math.atan2(sample.y - lidarY - robotY, sample.x - lidarX - robotX)));
        return sortedSamples;
    }

    // Calculates the world position of a lidar sample
    private Sample calcSample(double rotation, double distance) {
        // Undo the rotation of the samples that occurs when the robot turns
        rotation += robotAngle;

        // Get the X and Y positions of the point relative to the robot
        double x = Math.cos(rotation) * distance;
        double y = Math.sin(rotation) * distance;

        // Undo the translation of the samples that occurs when the robot moves
        x += robotX;
        y += robotY;

        // Account for the position of the lidar on the robot
        x += lidarX;
        y += lidarY;

        return new Sample((float) x, (float) y);
    }

    private void drawSampleSet(List<Sample> samples, int color) {
        if (DRAW_POINTS) {
            // Draw sample points
            stroke(color);
            strokeWeight(8);
            for (Sample sample : samples) {
                point(sample.x, sample.y);
            }
        }

        if (DRAW_LINES) {
            // Draw lines connecting points
            stroke(color);
            strokeWeight(3);
            noFill();
            beginShape();
            for (Sample sample : samples) {
                vertex(sample.x, sample.y);
            }
            endShape(CLOSE);
        }
    }

    // Sample data from the lidar
    private void handleLidarScanMessage(DataInputStream in) throws IOException {
        int quality = in.readInt();
        double angle = in.readDouble();
        double distance = in.readDouble();

        // Skip bad samples
        if (quality == 0 || distance == 0)
            return;

        // Convert from millimeters to meters
        distance /= 1000.0;

        // Angle from lidar is in clockwise degrees from the -X axis, so
        // convert it to counterclockwise radians from the X axis.
        angle = Math.toRadians(-angle + 180);

        // Calculate and store the sample
        incomingSamples.add(calcSample(angle, distance));
    }

    // When a new scanning round starts
    private void handleLidarScanStartMessage() {
        // Sort the current samples and add it to the past samples
        List<Sample> sortedSamples = sortSamples(incomingSamples);
        pastSamples.add(sortedSamples);

        // If there are too many past samples, remove the oldest
        if (pastSamples.size() > MAX_PAST_SAMPLES) {
            pastSamples.remove(0);
        }

        // A new scan has started, so display the samples we received last scan
        lidarSamples = incomingSamples;
        incomingSamples = new HashSet<>();
    }

    // Position of the robot from the localization system
    private void handleRobotLocationMessage(DataInputStream in) throws IOException {
        robotX = (float) in.readDouble();
        robotY = (float) in.readDouble();
        robotAngle = (float) in.readDouble();

        // Calculate the position of the lidar relative to the robot
        float sin = sin(robotAngle);
        float cos = cos(robotAngle);
        lidarX = LIDAR_OFFSET_X * cos - LIDAR_OFFSET_Y * sin;
        lidarY = LIDAR_OFFSET_X * sin + LIDAR_OFFSET_Y * cos;
    }

    // Handles messages from the message server
    private void messageCallback(String type, byte[] data) {
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(data));
        try {
            switch (type) {
                case "Lidar:Scan":
                    handleLidarScanMessage(in);
                    break;
                case "Lidar:ScanStart":
                    handleLidarScanStartMessage();
                    break;
                case "RoboRIO:Location":
                    handleRobotLocationMessage(in);
                    break;
            }

            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Adjust the stroke weight to appear the same regardless of zoom level
    @Override
    public void strokeWeight(float weight) {
        super.strokeWeight(weight / zoom);
    }

    @Override
    public void keyPressed() {
        msg.sendMessage("Lidar:Start", new byte[0]);
    }

    @Override
    public void mouseWheel(MouseEvent event) {
        float e = event.getCount();
        zoom += zoom * 0.1f * e;
    }

    @Override
    public void mouseDragged() {
        panX += (mouseX - pmouseX) / zoom;
        panY += (mouseY - pmouseY) / zoom;
    }

    private static class Sample {
        public final float x;
        public final float y;

        public Sample(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }

    public static void main(String[] args) {
        PApplet.main(NewDemo.class.getName());
    }
}
