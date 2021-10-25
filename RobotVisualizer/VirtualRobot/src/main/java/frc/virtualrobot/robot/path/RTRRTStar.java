package frc.virtualrobot.robot.path;

import java.util.*;

public class RTRRTStar {
    private final Environment env;
    private final float closeRadius;
    private final float nearRadius;
    private final int kMax;
    private final double rS;
    private final double alpha;
    private final double beta;
    private final double targetRadius;

    public Tree tree; // TODO: Should not be public
    private Point agentPos;
    private Point goalPos;
    private Point targetNode;
    private final Deque<Point> qR;
    private final Deque<Point> qS;
    private final Set<Point> pushedToQs;
    private long stopTime;

    public RTRRTStar(Environment env, Point startPos, Point goalPos, float closeRadius, float nearRadius, int kMax, double rS, double alpha, double beta, double targetRadius) {
        this.env = env;
        startPos.setCost(0);
        tree = new Tree(startPos);
        agentPos = startPos;
        this.goalPos = goalPos;
        this.closeRadius = closeRadius;
        this.nearRadius = nearRadius;
        this.kMax = kMax;
        this.rS = rS;
        this.alpha = alpha;
        this.beta = beta;
        this.targetRadius = targetRadius;

        qR = new ArrayDeque<>();
        qS = new ArrayDeque<>();
        pushedToQs = new HashSet<>();
    }

    private Point genSamplePosLineTo() {
        Point closest = getClosestPoint(goalPos);
        return closest.lerpTo(goalPos, Math.random());
    }

    private Point genSamplePosUniform() {
        double x = Math.random() * (env.getRight() - env.getLeft()) + env.getLeft();
        double y = Math.random() * (env.getBottom() - env.getTop()) + env.getTop();
        return new Point(x, y);
    }

    private Point genSamplePosEllipsis() {
        // TODO: Actually implement
        return genSamplePosUniform();
    }

    private Point genSamplePos() {
        double pR = Math.random();

        if (pR > 1 - alpha) {
            return genSamplePosLineTo();
        } else if (pR <= (1 - alpha) / beta || !tree.containsPoint(goalPos)) {
            return genSamplePosUniform();
        } else {
            return genSamplePosEllipsis();
        }
    }

    private Point getClosestPoint(Point p) {
        Point closest = null;
        double closestDist = Double.POSITIVE_INFINITY;

        for (Point point : tree.getPoints()) {
            double dist = p.distanceToSq(point);
            if (dist < closestDist) {
                closest = point;
                closestDist = dist;
            }
        }

        return closest;
    }

    private Set<Point> findNodesNear(Point p) {
        double area = (env.getRight() - env.getLeft()) * (env.getBottom() - env.getTop());
        double dist = area * kMax / (Math.PI * tree.getPointCount());
        dist = Math.max(dist, rS);
        Set<Point> near = new HashSet<>();

        for (Point point : tree.getPoints()) {
            if (p.distanceToSq(point) <= dist) {
                near.add(point);
            }
        }

        return near;
    }

    private double getCost(Point p) {
        double cost = p.getCost();
        if (cost >= 0) {
            return cost;
        }

        // The stack allows parent nodes' costs to be precalculated as well
        Deque<Point> pointStack = new ArrayDeque<>();
        while (p.getCost() < 0) {
            pointStack.push(p);
            p = tree.getParent(p);
        }
        cost = p.getCost();

        while (pointStack.size() > 0) {
            Point p2 = pointStack.pop();
            cost += p.distanceTo(p2); // Might be able to make squared
            p2.setCost(cost);
            p = p2;
        }

        return cost;
    }

    private void invalidateCost(Point p) {
        Queue<Point> toInvalidate = new ArrayDeque<>();
        toInvalidate.add(p);

        while (toInvalidate.size() > 0) {
            Point point = toInvalidate.remove();
            point.setCost(-1);
            toInvalidate.addAll(tree.getChildren(point));
        }
    }

    private void addNodeToTree(Point xNew, Point xClosest, Set<Point> xNearSet) {
        Point xMin = xClosest;
        double cMin = getCost(xClosest) + xClosest.distanceTo(xNew);

        for (Point xNear : xNearSet) {
            double cNew = getCost(xNear) + xNear.distanceTo(xNew);
            if (cNew < cMin && env.isLinePassable(xNear, xNew)) {
                cMin = cNew;
                xMin = xNear;
            }
        }

        tree.addChild(xMin, xNew);

        if (xNew.distanceTo(goalPos) < targetRadius) {
            targetNode = xNew;
        }
    }

    private void rewireRandomNode() {
        while (System.currentTimeMillis() < stopTime && !qR.isEmpty()) {
            Point xR = qR.removeFirst();
            Set<Point> xNearSet = findNodesNear(xR);
            for (Point xNear : xNearSet) {
                double cOld = getCost(xNear);
                double cNew = getCost(xR) + xR.distanceTo(xNear);
                if (cNew < cOld && env.isLinePassable(xR, xNear)) {
                    tree.remove(xNear);
                    tree.addChild(xR, xNear);
                    qR.addLast(xNear);
                    invalidateCost(xNear);
                }
            }
        }
    }

    private void rewireFromRoot() {
        if (qS.isEmpty()) {
            qS.add(tree.getRoot());
            //pushedToQs.clear(); // TODO: remove
        }

        while (System.currentTimeMillis() < stopTime && !qS.isEmpty()) {
            Point xS = qS.remove();
            Set<Point> xNearSet = findNodesNear(xS);
            for (Point xNear : xNearSet) {
                double cOld = getCost(xNear);
                double cNew = getCost(xS) + xS.distanceTo(xNear);
                if (cNew < cOld && env.isLinePassable(xS, xNear)) {
                    tree.remove(xNear);
                    tree.addChild(xS, xNear);
                    invalidateCost(xNear);
                }

                if (!pushedToQs.contains(xNear)) {
                    qS.push(xNear);
                    pushedToQs.add(xNear);
                }
            }
        }
    }

    private void expandAndRewireTree() {
        Point xRand = genSamplePos();
        Point xClosest = getClosestPoint(xRand);

        if (env.isLinePassable(xClosest, xRand)) {
            Set<Point> xNear = findNodesNear(xRand);
            if (xNear.size() < kMax || xClosest.distanceToSq(xRand) > rS * rS) {
                addNodeToTree(xRand, xClosest, xNear);
                qR.addFirst(xRand);
            } else {
                qR.addFirst(xClosest);
            }
            rewireRandomNode();
        }
        rewireFromRoot();
    }

    private List<Point> planPathToGoal() {
        List<Point> path;

        if (targetNode != null) {
            Deque<Point> pathQueue = new ArrayDeque<>();
            Point currentNode = targetNode;
            while (currentNode != null) {
                pathQueue.addFirst(currentNode);
                currentNode = tree.getParent(currentNode);
            }
            path = new ArrayList<>(pathQueue);
        } else {
            path = new ArrayList<>();
            path.add(goalPos);
        }

        return path;
    }

    private void setTreeRoot(Point newRoot) {
        tree.setRoot(newRoot);
        pushedToQs.clear();
        qS.clear();
        invalidateCost(newRoot);
        newRoot.setCost(0);
    }

    public List<Point> runForTime(long millis) {
        // Expand and rewire the tree while time is remaining
        stopTime = System.currentTimeMillis() + millis;
        while (System.currentTimeMillis() < stopTime) {
            expandAndRewireTree();
        }

        List<Point> path = planPathToGoal();
        if (path.get(0).distanceToSq(agentPos) < closeRadius * closeRadius) {
            path.remove(0);
            if (path.isEmpty()) {
                return path;
            }
            setTreeRoot(path.get(0));
        }

        return path;
    }

    public void setAgentPos(double x, double y) {
        agentPos = new Point(x, y);
    }

    public void setGoalPos(double x, double y){
        goalPos = new Point(x, y);
        targetNode = null;

        Point closeNode = getClosestPoint(goalPos);
        if (closeNode.distanceToSq(goalPos) < targetRadius * targetRadius) {
            targetNode = closeNode;
        }
    }
}
