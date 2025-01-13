package ui.utils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;

public class TaskBarIconUtil {

    private static final String ICON_PATH = "src/resources/images/Rokue-likelogo4.png";

    private TaskBarIconUtil() {
        // Private constructor to prevent instantiation
    }

    public static void setMacTaskbarIcon() {
        try {
            BufferedImage logoImage = ImageIO.read(new File(ICON_PATH));
            Taskbar taskbar = Taskbar.getTaskbar();
            taskbar.setIconImage(logoImage);
        } catch (UnsupportedOperationException e) {
            System.err.println("The Taskbar feature is not supported on this platform: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Failed to load the taskbar icon image: " + e.getMessage());
        }
    }

    public static void setWindowsTaskbarIcon(JFrame frame) {
        try {
            frame.setIconImage(Toolkit.getDefaultToolkit().getImage(ICON_PATH));
        } catch (Exception e) {
            System.err.println("Failed to set taskbar icon on Windows: " + e.getMessage());
        }


    }
}
