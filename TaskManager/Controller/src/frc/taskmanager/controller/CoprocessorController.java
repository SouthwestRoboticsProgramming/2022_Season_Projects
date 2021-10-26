package frc.taskmanager.controller;

import frc.taskmanager.client.Coprocessor;

import javax.swing.*;
import javax.swing.text.PlainDocument;
import java.awt.*;

public class CoprocessorController {
    private static final int WIDTH = 640;
    private static final int HEIGHT = 480;

    // Main method of this class.
    public void run() {
        JFrame frame = createWindow();

        // Connect to the coprocessor
        Coprocessor cp = null;
        while (cp == null) {
            CoprocessorConnectionParams params = showConnectionPopup();
            String host = params.getHost();
            int port = params.getPort();

            Coprocessor attempt = new Coprocessor(host, port);
            try {
                attempt.connect();
                cp = attempt;
            } catch (Throwable e) {
                System.err.println("Connection error:");
                e.printStackTrace();
                JOptionPane.showMessageDialog(null,
                        "Failed to connect to the coprocessor at " + host + ":" + port +
                            ".\nMake sure it is turned on, that you are connected to the correct network, \n" +
                            "and that the TaskManager server is running on the correct port. \n" +
                            "You can check the controller log for a more detailed error report.",
                        "Error Connecting", JOptionPane.ERROR_MESSAGE);
            }
        }

        System.out.println("Connected to coprocessor");

        // Create view panel
        TaskViewPanel panel = new TaskViewPanel(cp);

        // Add view panel to frame
        frame.getContentPane().add(panel);
        //frame.pack();
        frame.revalidate();
        frame.repaint();
    }

    // Creates the window that the controller will run in.
    private JFrame createWindow() {
        // Create window
        JFrame frame = new JFrame("Coprocessor Controller");
        frame.setSize(WIDTH, HEIGHT);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setVisible(true);
        return frame;
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
        int port = portField.getText().length() > 0 ? Integer.parseInt(portField.getText()) : 0;

        // Return the output
        return new CoprocessorConnectionParams(host, port);
    }
}
