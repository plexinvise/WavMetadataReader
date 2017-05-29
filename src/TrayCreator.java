import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;


/**
 * Created by plexinvise on 5/28/17.
 */
public class TrayCreator {

    private static String about;
    static WavMetadataReader wavReader;
    static Logger logger = Logger.getLogger(WavMetadataReader.class);


    public TrayCreator() {
        PropertyConfigurator.configure(Variables.LOG4J_PROPS);
    }

    public static void main(String[] args) throws IOException {
        wavReader = new WavMetadataReader();
        StringBuilder builder = new StringBuilder();
        about = builder.append(FileUtils.readFileToString(
                new File("..//WavMetadataReader//about.txt"), "UTF8"))
                .toString();
        //Check the SystemTray is supported
        if (!SystemTray.isSupported()) {
            System.out.println("SystemTray is not supported");
            return;
        }
        final PopupMenu popup = new PopupMenu();
        final TrayIcon trayIcon =
                new TrayIcon(Toolkit.getDefaultToolkit().createImage(
                        "..//WavMetadataReader//1496030759_CAD.png"));
        final SystemTray tray = SystemTray.getSystemTray();

        //Adding components to pop-up menu
        MenuItem aboutItem = new MenuItem("About");
        MenuItem run = new MenuItem("Run");
        MenuItem exitItem = new MenuItem("Exit");

        //Adding components to popup menu
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
                try {
                    wavReader.scanFiles();
                } catch (IOException e1) {
                    logger.error(e1);
                } catch (UnsupportedAudioFileException e1) {
                    logger.error(e1);
                }
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
