package frc.virtualrobot.robot.path;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Tree {
    private final Set<Point> points;
    private final Map<Point, Set<Point>> childMap;
    private final Map<Point, Point> parentMap;

    private Point root;

    public Tree(Point root) {
        points = new HashSet<>();
        points.add(root);
        this.root = root;
        childMap = new HashMap<>();
        parentMap = new HashMap<>();
    }

    public Set<Point> getPoints() {
        return new HashSet<>(points);
    }

    public int getPointCount() {
        return points.size();
    }

    public void setRoot(Point newRoot) {
        remove(newRoot);
        addChild(newRoot, root);
        root = newRoot;
    }

    public boolean containsPoint(Point p) {
        return points.contains(p);
    }

    public Set<Point> getChildren(Point parent) {
        return new HashSet<>(getChildSet(parent));
    }

    public Point getParent(Point p) {
        return parentMap.get(p);
    }

    public void addChild(Point parent, Point child) {
        parentMap.put(child, parent);
        getChildSet(parent).add(child);
        points.add(child);
    }

    public void remove(Point point) {
        Point parent = parentMap.remove(point);
        getChildSet(parent).remove(point);
        points.remove(point);
    }

    private Set<Point> getChildSet(Point parent) {
        return childMap.computeIfAbsent(parent, k -> new HashSet<>());
    }

    public Point getRoot() {
        return root;
    }
}
