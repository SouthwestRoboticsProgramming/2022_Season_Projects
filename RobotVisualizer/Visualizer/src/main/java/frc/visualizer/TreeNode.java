package frc.visualizer;

import java.util.List;

public class TreeNode {
    private final double x, y;
    private final List<TreeNode> children;
    private int childCount;

    public TreeNode(double x, double y, List<TreeNode> children) {
        this.x = x;
        this.y = y;
        this.children = children;

        childCount = children.size();
        for (TreeNode node : children) {
            childCount += node.childCount;
        }
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public List<TreeNode> getChildren() {
        return children;
    }

    public int getChildCount() {
        return childCount;
    }
}
