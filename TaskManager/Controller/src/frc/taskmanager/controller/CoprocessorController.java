package frc.taskmanager.controller;

import javax.swing.*;
import javax.swing.text.PlainDocument;
import java.awt.*;

public class CoprocessorController {
    private static final int WIDTH = 640;
    private static final int HEIGHT = 480;

    public void run() {
        createWindow();
        CoprocessorConnectionParams params = showConnectionPopup();

        System.out.println(params);
    }

    private void createWindow() {
        // Create window
        JFrame frame = new JFrame("Coprocessor Controller");
        frame.setSize(WIDTH, HEIGHT);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setVisible(true);
    }

    // Shows a popup window that asks for the coprocessor connection parameters.
    private CoprocessorConnectionParams showConnectionPopup() {
        // Create the fields
        JTextField hostField = new JTextField();
        JTextField portField = new JTextField();

        // Port field can only accept numbers
        PlainDocument doc = (PlainDocument) portField.getDocument();
        doc.setDocumentFilter(new NumberFilter());

        // Set up the panel
        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Host:"));
        panel.add(hostField);
        panel.add(new JLabel("Port:"));
        panel.add(portField);

        // Show the dialog
        JOptionPane.showMessageDialog(null, panel, "Connect to Coprocessor", JOptionPane.PLAIN_MESSAGE);

        // Read the output
        String host = hostField.getText();
        int port = Integer.parseInt(portField.getText());

        // Return the output
        return new CoprocessorConnectionParams(host, port);
    }
}
