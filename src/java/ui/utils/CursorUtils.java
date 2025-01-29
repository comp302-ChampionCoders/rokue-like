package ui.utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

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

        public static Cursor createCustomCursor(InputStream imageStream) {
            try {
                if (imageStream == null) {
                    throw new IOException("Image stream is null");
                }
                BufferedImage cursorImage = ImageIO.read(imageStream);
                Toolkit toolkit = Toolkit.getDefaultToolkit();
                return toolkit.createCustomCursor(cursorImage, new Point(0, 0), "CustomCursor");
            } catch (IOException e) {
                System.err.println("Failed to create custom cursor: " + e.getMessage());
                return Cursor.getDefaultCursor();
            }
    }
    }


