package frc.taskmanager.controller;

import javax.swing.BoundedRangeModel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.text.DefaultCaret;
import javax.swing.text.JTextComponent;
import java.awt.Component;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

public class SmartScroller implements AdjustmentListener {
    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;

    public static final int START = 0;
    public static final int END = 1;

    private final int viewportPosition;

    private boolean adjustScrollBar = true;

    private int previousValue = -1;
    private int previousMaximum = -1;

    public SmartScroller(JScrollPane scrollPane) {
        this(scrollPane, VERTICAL, END);
    }

    public SmartScroller(JScrollPane scrollPane, int viewportPosition) {
        this(scrollPane, VERTICAL, viewportPosition);
    }

    public SmartScroller(JScrollPane scrollPane, int scrollDirection, int viewportPosition) {
        if (scrollDirection != HORIZONTAL && scrollDirection != VERTICAL) {
            throw new IllegalArgumentException("Invalid scroll direction specified");
        }

        if (viewportPosition != START && viewportPosition != END) {
            throw new IllegalArgumentException("Invalid viewport position specified");
        }

        this.viewportPosition = viewportPosition;

        JScrollBar scrollBar;
        if (scrollDirection == HORIZONTAL) {
            scrollBar = scrollPane.getHorizontalScrollBar();
        } else {
            scrollBar = scrollPane.getVerticalScrollBar();
        }

        scrollBar.addAdjustmentListener(this);

        Component view = scrollPane.getViewport().getView();

        if (view instanceof JTextComponent) {
            JTextComponent textComponent = (JTextComponent) view;
            DefaultCaret caret = (DefaultCaret) textComponent.getCaret();
            caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
        }
    }

    @Override
    public void adjustmentValueChanged(AdjustmentEvent e) {
        SwingUtilities.invokeLater(() -> checkScrollBar(e));
    }

    private void checkScrollBar(AdjustmentEvent e) {
        JScrollBar scrollBar = (JScrollBar) e.getSource();
        BoundedRangeModel listModel = scrollBar.getModel();
        int value = listModel.getValue();
        int extent = listModel.getExtent();
        int maximum = listModel.getMaximum();

        boolean valueChanged = previousValue != value;
        boolean maximumChanged = previousMaximum != maximum;

        if (valueChanged && !maximumChanged) {
            if (viewportPosition == START) {
                adjustScrollBar = value != 0;
            } else {
                adjustScrollBar = value + extent >= maximum;
            }
        }

        if (adjustScrollBar && viewportPosition == END) {
            scrollBar.removeAdjustmentListener(this);
            value = maximum - extent;
            scrollBar.setValue(value);
            scrollBar.addAdjustmentListener(this);
        }

        if (adjustScrollBar && viewportPosition == START) {
            scrollBar.removeAdjustmentListener(this);
            value = value + maximum - previousMaximum;
            scrollBar.setValue(value);
            scrollBar.addAdjustmentListener(this);
        }

        previousValue = value;
        previousMaximum = maximum;
    }
}
