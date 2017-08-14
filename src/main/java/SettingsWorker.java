/**
 * SettingsWorker aimed to run separate JFrame to set properties for application.
 * Properties storing in the constants.properties file in the same folder as .jar.
 *
 * Created by plexinvise on 8/13/17.
 */

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.Properties;

public class SettingsWorker {
    private JButton chooseOutput;
    private JButton chooseInput;
    private JFileChooser chooser;
    private static Logger logger = Logger.getLogger(SettingsWorker.class);
    private Properties constants = new Properties();

    public SettingsWorker() throws IOException {
        /*
         * To run from IDE need to replace propIn initialization with
         * InputStream propIn = new FileInputStream("./constants.properties");
         */
        InputStream propIn = new FileInputStream(
                new File(getClass().getProtectionDomain().getCodeSource()
                        .getLocation().getPath()).getParentFile().getPath()
                        + "/constants.properties");

        constants.load(propIn);
        if (!constants.contains("log4jProps")) {
            PropertyConfigurator.configure(getClass().getResourceAsStream("log4j.properties"));
        } else {
            PropertyConfigurator.configure(constants.getProperty("log4jProps"));
        }
    }

    @NotNull
    private Dimension getPreferredSize() {
        return new Dimension(400, 200);
    }

    public void openSettings() {
        JFrame frame = new JFrame("Settings");

        JPanel panel = new JPanel();

        chooseInput = new JButton("Set input folder");
        panel.add(chooseInput, 0);

        chooseOutput = new JButton("Choose output file");
        panel.add(chooseOutput, 1);

        JTextArea textArea = new JTextArea(8, 30);
        textArea.setLineWrap(true);
        panel.add(textArea, 2);

        frame.getContentPane().add(panel, "Center");
        frame.setSize(getPreferredSize());
        frame.setVisible(true);


        // Choosing input folder
        chooseInput.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chooser = new JFileChooser();
                chooser.setCurrentDirectory(new File(
                        getClass().getProtectionDomain().getCodeSource().getLocation().getPath()));
                chooser.setDialogTitle("Set input folder");
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

                // only folders might be chosen
                chooser.setAcceptAllFileFilterUsed(false);

                if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {

                    writeProperties("wavInputFolder", chooser.getSelectedFile().getAbsolutePath());

                    textArea.append("Input folder set to: "
                            + chooser.getSelectedFile().getAbsolutePath() + "\n");
                } else {
                    textArea.append("Nothing been selected" + "\n");
                }
            }
        });

        // Choosing output file
        chooseOutput.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chooser = new JFileChooser();
                chooser.setCurrentDirectory(new File(
                        getClass().getProtectionDomain().getCodeSource().getLocation().getPath()));
                chooser.setDialogTitle("Choose output file");
                chooser.setFileSelectionMode(JFileChooser.SAVE_DIALOG);
                if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {

                    writeProperties("outputFilePath", chooser.getSelectedFile().getAbsolutePath());

                    textArea.append("Output file path set to: "
                            + chooser.getSelectedFile().getAbsolutePath() + "\n");
                } else {
                    textArea.append("Nothing been selected" + "\n");
                }
            }
        });

    }

    private void writeProperties(String propertyName, String propertyValue) {
        try {
            constants.setProperty(propertyName, propertyValue);

            /*
             * To run from IDE file should be initialized with
             * new File(./constants.properties)
             */
            File file = new File(new File(getClass().getProtectionDomain().getCodeSource()
                    .getLocation().getPath()).getParentFile().getPath()
                    + "/constants.properties");

            FileOutputStream fileOut = new FileOutputStream(file);
            constants.store(fileOut, null);
            fileOut.close();
        } catch (IOException e) {
            logger.error(e);
        }

    }
}

