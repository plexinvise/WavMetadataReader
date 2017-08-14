/**
 * TrayCreator class purpose is to run program in the thread
 * and wrap it with tray icon so user can interact and start a new program run
 * by clicking on the menu item. On this stage methods in the class
 * creating Menu with 3 options: Run, About, Exit
 * Run - calling method scanFiles() from the WavMetadataReader class
 * About - showing JPanel with text String about which stored in the about.txt file
 * Exit - Removing trayIcon and calling System.exit so the program execution stopping
 *
 * Created by plexinvise on 5/28/17.
 */

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.Properties;

import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;

public class TrayCreator {

    private String about;
    private WavMetadataReader wavReader;
    private SettingsWorker settingsWorker;
    private static Logger logger = Logger.getLogger(TrayCreator.class);
    private Properties constants = new Properties();

    public TrayCreator() throws IOException {
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

    //Running trayInit with SwingUtilities method
    public static void main(String[] args) throws IOException {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    new TrayCreator().trayInit();
                } catch (IOException e) {
                    logger.error(e);
                }
            }
        });
    }

    private void trayInit() throws IOException {
        wavReader = new WavMetadataReader();
        StringBuilder builder = new StringBuilder();

        //Getting text for "about" dialog
        InputStream inputStream = getClass().getResourceAsStream("about.txt");
        byte[] buffer = new byte[inputStream.available()];
        IOUtils.read(inputStream, buffer);
        about = builder.append(IOUtils.toString(buffer, "UTF-8")).toString();

        //Check the SystemTray is supported
        if (!SystemTray.isSupported()) {
            System.out.println("SystemTray is not supported");
            return;
        }

        //SystemTray and PopupMenu is final cause it is only might be initialized once
        //TrayIcon might be changed during the program running
        final PopupMenu popup = new PopupMenu();

        //Will not be null
        TrayIcon trayIcon = new TrayIcon(Toolkit.getDefaultToolkit()
                .createImage(getClass().getResource("1496030759_CAD.png")));

        final SystemTray tray = SystemTray.getSystemTray();

        //Adding components to pop-up menu
        MenuItem aboutItem = new MenuItem("About");
        MenuItem settingsItem = new MenuItem("Settings");
        MenuItem run = new MenuItem("Run");
        MenuItem exitItem = new MenuItem("Exit");

        //Adding components to popup menu
        popup.add(settingsItem);
        popup.add(run);
        popup.addSeparator();
        popup.add(aboutItem);
        popup.addSeparator();
        popup.add(exitItem);

        trayIcon.setPopupMenu(popup);

        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            System.out.println("TrayIcon could not be added.");
            return;
        }

        run.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (constants.contains("wavInputFolder")&&constants.contains("outputFilePath")) {
                    try {
                        wavReader.scanFiles();
                    } catch (IOException | UnsupportedAudioFileException e1) {
                        logger.error(e1);
                    }
                } else {
                    JOptionPane.showMessageDialog(null,
                            "Run settings first to configure input and output paths");
                }
            }
        });

        settingsItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                settingsWorker.openSettings();
            }
        });

        aboutItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, about);
            }
        });

        exitItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tray.remove(trayIcon);
                System.exit(0);
            }
        });
    }
}