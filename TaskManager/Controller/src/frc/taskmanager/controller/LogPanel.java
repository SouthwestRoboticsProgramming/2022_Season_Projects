package frc.taskmanager.controller;

import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.Color;
import java.awt.Font;

public class LogPanel extends JScrollPane {
    private static final String OUT_PREFIX = "[OUT] ";
    private static final String ERR_PREFIX = "[ERR] ";

    private final JTextPane pane;
    private final StyledDocument doc;
    private final Style out;
    private final Style err;

    public LogPanel() {
        super();

        pane = new JTextPane();
        pane.setEditable(false);
        pane.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        super.setViewportView(pane);
        new SmartScroller(this);

        doc = pane.getStyledDocument();
        out = pane.addStyle("out", null);
        err = pane.addStyle("err", null);
        StyleConstants.setForeground(err, Color.RED);
    }

    public void logOut(String message) {
        addLine(OUT_PREFIX + message + "\n", out);
    }

    public void logErr(String message) {
        addLine(ERR_PREFIX + message + "\n", err);
    }

    public void clear() {
        pane.setText("");
    }

    public void setDisabled(boolean disabled) {
        pane.setEnabled(!disabled);
    }

    private void addLine(String line, Style style) {
        try {
            doc.insertString(doc.getLength(), line, style);
        } catch (BadLocationException e) {
            // Ignore
        }
    }
}
