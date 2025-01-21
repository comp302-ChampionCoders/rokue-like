package domain.enchantments;

import domain.behaviors.GridElement;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Lure implements GridElement {
    private int x;
    private int y;
    private BufferedImage image;

    public Lure(int x, int y) {
        this.x = x;
        this.y = y;
        loadImage();
    }

    private void loadImage() {
        try {
            image = ImageIO.read(new File("src/resources/images/lure32x32.png"));
        } catch (IOException e) {
            System.err.println("Failed to load Lure image: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public BufferedImage getImage() {
        return image;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "Lure at position (" + x + ", " + y + ")";
    }

    // Additional methods if required for integration
}
