package frc.lidar.demo;

import frc.lidar.lib.Lidar;
import frc.lidar.lib.ScanEntry;
import frc.messenger.client.MessengerClient;
import processing.core.PApplet;
import processing.event.MouseEvent;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.*;

public class Demo extends PApplet {
    private MessengerClient msg;
    private Set<ScanEntry> scan, newScan;
    private float scale = 0.5f;
    private float tx = 0, ty = 0;
    private double rx, ry, rangle;

    @Override
    public void settings() {
        size(1280, 720, P2D);
    }

    @Override
    public void setup() {
        msg = new MessengerClient("10.21.29.17", 8341, "Lidar-Visualizer");
        msg.listen("Lidar:Ready");
        msg.listen("Lidar:ScanStart");
        msg.listen("Lidar:Scan");
        msg.listen("RoboRIO:Location");
        msg.setCallback(this::messageCallback);

        scan = new HashSet<>();
        newScan = new HashSet<>();

        ellipseMode(CENTER);
    }

    private void messageCallback(String type, byte[] data) {
        //System.out.println(type);
        switch (type) {
            case "Lidar:Ready":
                msg.sendMessage("Lidar:Start", new byte[0]);
                break;
            case "Lidar:ScanStart":
                scan = newScan;
                newScan = new HashSet<>();
                break;
            case "Lidar:Scan": {
                DataInputStream in = new DataInputStream(new ByteArrayInputStream(data));
                try {
                    int quality = in.readInt();
                    double angle = in.readDouble() - Math.toDegrees(rangle);
                    double distance = in.readDouble();

                    if (quality == 0 || distance == 0) break;

                    newScan.add(new ScanEntry(quality, angle, distance));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
            case "RoboRIO:Location": {
                DataInputStream in = new DataInputStream(new ByteArrayInputStream(data));
                try {
                    rx = in.readDouble();
                    ry = in.readDouble();
                    rangle = in.readDouble();
                    System.out.println(rangle);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    @Override
    public void draw() {
        msg.read();

        background(0);
        translate(width / 2f, height / 2f);

        scale(scale);
        translate(tx, ty);

        stroke(64);
        strokeWeight(1 / scale);
        line(-width / scale - tx, 0, width / scale - tx, 0);
        line(0, height / scale - ty, 0, -height / scale - ty);

        noFill();
        stroke(255, 0, 0);
        strokeWeight(8 / scale);

        List<ScanEntry> scanList = new ArrayList<>(scan);
        scanList.sort(Comparator.comparingDouble(ScanEntry::getAngle));

        beginShape(POINTS);
        for (ScanEntry entry : scanList) {
            float x = (float) (Math.cos(Math.toRadians(entry.getAngle())) * entry.getDistance());
            float y = (float) (Math.sin(Math.toRadians(entry.getAngle())) * entry.getDistance());

            vertex(x, y);
        }
        endShape();

        strokeWeight(3 / scale);
        beginShape();
        for (ScanEntry entry : scanList) {
            float x = (float) (Math.cos(Math.toRadians(entry.getAngle())) * entry.getDistance());
            float y = (float) (Math.sin(Math.toRadians(entry.getAngle())) * entry.getDistance());

            vertex(x, y);
        }
        endShape(CLOSE);

        noFill();
        stroke(255);
        ellipse(0, 0, 340, 340);

        fill(255);
        stroke(255);
        rotate((float) -rangle - PI / 2);
        triangle(-40, 200, 40, 200, 0, -200);
    }

    @Override
    public void mouseWheel(MouseEvent event) {
        float e = event.getCount();
        scale += scale * 0.1f * e;
    }

    @Override
    public void mouseDragged() {
        tx += (mouseX - pmouseX) / scale;
        ty += (mouseY - pmouseY) / scale;
    }

    @Override
    public void keyPressed() {
        msg.sendMessage("Lidar:Stop", new byte[0]);
        exit();
    }

    public static void main(String[] args) {
        PApplet.main(Demo.class.getName());
    }
}
