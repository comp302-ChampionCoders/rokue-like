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
