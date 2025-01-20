package domain.gameobjects;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

import domain.behaviors.GridElement;

import javax.imageio.ImageIO;

public class GameObject implements GridElement, Serializable {
    protected int x;
    protected int y;
    protected boolean isActive;
    private transient Image image;
    private String imagePath;

    public GameObject(int x, int y, Image image, String imagePath) {
        this.x = x;
        this.y = y;
        this.image = image;
        this.imagePath = imagePath;
        this.isActive = true;
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject(); // Serileştirilen diğer alanları yükle
        loadImage(imagePath);
    }

    public void loadImage(String path) {
        try {
            this.image = ImageIO.read(new File(path)); // Görsel yükleme
        } catch (IOException e) {
            System.err.println("Failed to load image: " + path);
            this.image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB); // Varsayılan boş görsel
        }
    }

    @Override
    public int getX() {
        return x;
    }

    public Image getImage(){
        return image;
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
    
    public boolean isActive() {
        return isActive;
    }
    
    public void setActive(boolean active) {
        this.isActive = active;
    }
    

}