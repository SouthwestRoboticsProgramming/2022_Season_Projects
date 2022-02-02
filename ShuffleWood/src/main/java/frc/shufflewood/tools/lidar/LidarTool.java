package frc.shufflewood.tools.lidar;

import frc.shufflewood.App;
import frc.shufflewood.MessengerAccess;
import frc.shufflewood.gui.GuiContext;
import frc.shufflewood.gui.Vec2;
import frc.shufflewood.tools.Tool;
import processing.core.PConstants;
import processing.core.PGraphics;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.*;

public class LidarTool implements Tool {
    private final App app;
    private final MessengerAccess msg;
    private PGraphics g;
    private int lastW = -1, lastH = -1;
    private Set<ScanEntry> scan, newScan;

    public LidarTool(App app) {
        this.app = app;
        msg = app.getMessenger();

        msg.listen("Lidar:ScanStart", this::messageCallback);
        msg.listen("Lidar:Scan", this::messageCallback);

        scan = new HashSet<>();
        newScan = new HashSet<>();
    }

    private void messageCallback(String type, byte[] data) {
        switch (type) {
            case "Lidar:ScanStart":
                scan = newScan;
                newScan = new HashSet<>();
                break;
            case "Lidar:Scan": {
                DataInputStream in = new DataInputStream(new ByteArrayInputStream(data));
                try {
                    int quality = in.readInt();
                    double angle = in.readDouble();
                    double distance = in.readDouble();

                    if (quality == 0 || distance == 0) break;

                    newScan.add(new ScanEntry(quality, angle, distance));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    private void drawGraphics() {
        g.background(0);
        g.translate(g.width / 2f, g.height / 2f);

        g.scale(0.1f);

        List<ScanEntry> scanList = new ArrayList<>(scan);
        scanList.sort(Comparator.comparingDouble(ScanEntry::getAngle));

        g.stroke(0, 0, 255);
        g.strokeWeight(32);
        g.fill(0, 0, 64);
        g.beginShape();
        int size = scanList.size();
        for (int i = 0; i < size; i++) {
            ScanEntry last = scanList.get(i > 0 ? i - 1 : size - 1);
            ScanEntry current = scanList.get(i);
            ScanEntry next = scanList.get(i < size - 1 ? i + 1 : 0);

            float x1 = last.getX();
            float y1 = last.getY();
            float x0 = current.getX();
            float y0 = current.getY();
            float x2 = next.getX();
            float y2 = next.getY();

            float num = Math.abs((x2-x1)*(y1-y0)-(x1-x0)*(y2-y1));
            float den = (float) Math.sqrt((x2-x1)*(x2-x1)+(y2-y1)*(y2-y1));
            float off = num / den;
            float linearity = 1 / off;

            float x = (float) (Math.cos(Math.toRadians(current.getAngle())) * linearity * 200);
            float y = (float) (Math.sin(Math.toRadians(current.getAngle())) * linearity * 200);
            g.vertex(x, y);
        }
        g.endShape();

        g.stroke(255, 0, 0);
        g.strokeWeight(32);
        g.beginShape(PConstants.POINTS);
        for (ScanEntry entry : scanList) {
            float x = (float) (Math.cos(Math.toRadians(entry.getAngle())) * entry.getDistance());
            float y = (float) (Math.sin(Math.toRadians(entry.getAngle())) * entry.getDistance());

            g.vertex(x, y);
        }
        g.endShape();
    }

    @Override
    public void draw(GuiContext gui) {
        gui.begin("Lidar");

        gui.beginTable(false, false, 1, 1);
        if (gui.button("Start scan")) {
            msg.sendMessage("Lidar:Start", new byte[0]);
        }
        gui.tableNextColumn();
        if (gui.button("Stop scan")) {
            msg.sendMessage("Lidar:Stop", new byte[0]);
        }
        gui.endTable();

        Vec2 size = gui.getAvailableContentSize();
        int width = (int) size.x;
        int height = (int) size.y;
        if (width > 0 && height > 0) {
            if (width != lastW || height != lastH) {
                g = app.createGraphics(width, height, PConstants.P2D);
                lastW = width;
                lastH = height;
            }

            g.beginDraw();
            drawGraphics();
            g.endDraw();

            gui.image(g);
        }

        gui.end();
    }
}
