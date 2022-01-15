package frc.shufflewood;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.util.function.Consumer;

// IMPORTANT: This does not work on MacOS
public final class FileChooser {
    public static File chooseZipOrFolder(Consumer<File> callback) {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        chooser.setFileFilter(new FileNameExtensionFilter("ZIP Archive", "zip"));

        JFrame frame = new JFrame("Choose a File or Folder");
        frame.setSize(480, 360);

        chooser.addActionListener((event) -> {
            if (event.getActionCommand().equals(JFileChooser.APPROVE_SELECTION)) {
                callback.accept(chooser.getSelectedFile());
                frame.dispose();
            } else if (event.getActionCommand().equals(JFileChooser.CANCEL_SELECTION)) {
                callback.accept(null);
                frame.dispose();
            }
        });

        frame.getContentPane().add(chooser);
        frame.setVisible(true);

        return null;
    }

    private FileChooser() {
        throw new AssertionError();
    }
}
