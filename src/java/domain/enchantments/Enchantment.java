package domain.enchantments;

import domain.behaviors.Collectible;
import domain.behaviors.GridElement;
import domain.gameobjects.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public abstract class Enchantment implements Collectible, GridElement {
    private String name; 
    private boolean isActive;
    private int x,y;
    private long spawnTime;
    private static final long DISAPPEAR_TIME = 6000; //6s in ms 
    private BufferedImage image; // Image for the enchantment

    public Enchantment(String name, String imagePath) {
        this.name = name;
        this.isActive = false;
        
        loadImage(imagePath);
    }

    private void loadImage(String imagePath) {
        try {
            this.image = ImageIO.read(new File(imagePath));
        } catch (IOException e) {
            System.err.println("Failed to load image for enchantment: " + name);
            e.printStackTrace();
        }
    }

    @Override
    public void collect(Hero hero) {
        if (canBeCollected()) {
            disappear();
            applyEffect(hero);
        }
    }

    @Override
    public boolean canBeCollected() {
        return isActive;
    }

    @Override
    public void appear(int x, int y) {
        this.x = x;
        this.y = y;
        this.isActive = true;
        this.spawnTime = System.currentTimeMillis();
    }

    @Override
    public void disappear() {
        this.isActive = false;
    }
    @Override
    public String getType() { return name; }
    // GridElement Interface implementation
    @Override
    public int getX() {return x;}
    @Override
    public int getY() {return y;}
    
    @Override
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean isAvailable() {
        return isActive;
    }

    @Override
    public long getTimeRemaining() {
        if (!isActive) return -1;
        long elapsedTime = System.currentTimeMillis() - spawnTime;
        return Math.max(0, DISAPPEAR_TIME - elapsedTime);
    }

    public BufferedImage getImage() {
        return image;
    }

    public String getName() {return name;}
    public boolean isActive() {return isActive;}
    
    protected void activate() {isActive = true;}
    protected void deactivate() {isActive = false;}

    // Abstract method to define specific behavior
    public abstract void applyEffect(Hero hero);

    // Abstract method to undo the effect
    public abstract void removeEffect(Hero hero);
    public abstract long getEffectDuration();
}

