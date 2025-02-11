package ui.utils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import javax.swing.*;

public class TaskBarIconUtil {

    private static final String ICON_PATH = "/images/Rokue-likelogo4.png";

    private TaskBarIconUtil() {
        // Private constructor to prevent instantiation
    }

    public static void setMacTaskbarIcon() {
        try {
            InputStream iconStream = TaskBarIconUtil.class.getResourceAsStream(ICON_PATH);
            if (iconStream == null) {
                throw new IOException("Icon not found: " + ICON_PATH);
            }
            BufferedImage logoImage = ImageIO.read(iconStream);
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
            InputStream iconStream = TaskBarIconUtil.class.getResourceAsStream(ICON_PATH);
            if (iconStream == null) {
                throw new IOException("Icon not found: " + ICON_PATH);
            }
            Image logoImage = ImageIO.read(iconStream);
            frame.setIconImage(logoImage);
        } catch (IOException e) {
            System.err.println("Failed to set taskbar icon on Windows: " + e.getMessage());
        }
    }
}
