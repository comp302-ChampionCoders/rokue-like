package ui.utils;

import java.awt.*;
import java.io.File;

public class CursorUtils {

    public static Cursor createCustomCursor(String imagePath) {
        try {
            Toolkit toolkit = Toolkit.getDefaultToolkit();
            Image cursorImage = toolkit.getImage(imagePath);
            Point hotspot = new Point(0, 0);
            return toolkit.createCustomCursor(cursorImage, hotspot, "Custom Cursor");
        } catch (Exception e) {
            System.err.println("Failed to load custom cursor: " + e.getMessage());
            return Cursor.getDefaultCursor();
        }
    }
}
