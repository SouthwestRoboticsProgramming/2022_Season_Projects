package frc.lidar.demo;

import frc.lidar.lib.Lidar;
import frc.lidar.lib.ScanEntry;
import processing.core.PApplet;
import processing.event.MouseEvent;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Demo extends PApplet {
    private Lidar lidar;
    private Set<ScanEntry> scan, newScan;
    private float scale = 1;
    private float tx = 0, ty = 0;

    @Override
    public void settings() {
        fullScreen(P2D);
    }

    @Override
    public void setup() {
        lidar = new Lidar();
        lidar.getHealth().thenAccept(PApplet::println);

        scan = new HashSet<>();
        newScan = new HashSet<>();
        lidar.setScanDataCallback((entry) -> {
            if (entry.getQuality() == 0 || entry.getDistance() == 0) return;
            System.out.println(entry);
            newScan.add(entry);
        });
        lidar.setScanStartCallback(() -> {
            scan = newScan;
            newScan = new HashSet<>();
        });

        delay(1000);
        lidar.startScanning();
    }

    @Override
    public void draw() {
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
        strokeWeight(2 / scale);

        List<ScanEntry> scanList  = new ArrayList<>(scan);
        scanList.sort(Comparator.comparingDouble(ScanEntry::getAngle));

        beginShape(POINTS);
        for (ScanEntry entry : scanList) {
            float x = (float) (Math.cos(Math.toRadians(entry.getAngle())) * entry.getDistance());
            float y = (float) (Math.sin(Math.toRadians(entry.getAngle())) * entry.getDistance());

            vertex(x, y);
        }
        endShape();
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
        lidar.stopScanning();
        delay(1000);
        lidar.close();

        exit();
    }

    public static void main(String[] args) {
        PApplet.main(Demo.class.getName());
    }
}
