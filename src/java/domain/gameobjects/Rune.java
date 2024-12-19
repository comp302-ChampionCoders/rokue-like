package domain.gameobjects;

import domain.behaviors.Collectible;
import domain.behaviors.GridElement;

public class Rune implements Collectible, GridElement {
    private int x;
    private int y;
    private boolean isCollected;
    private boolean isAvailable;

    public Rune(int x, int y) {
        this.x = x;
        this.y = y;
        this.isAvailable = true;
        this.isCollected = false;
    }

    // GridElement interface implementation
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
    public void collect(Hero hero) {
        if (!isCollected && isAvailable) {
            isCollected = true;
            isAvailable = false;
            // hall will handle the unlocking the door
        }
    }

    public boolean isCollected() {
        return isCollected;
    }

    @Override
    public boolean canBeCollected() {
        return !isCollected && isAvailable;
    }

    @Override
    public String getType() {
        return "Rune";
    }

    @Override
    public boolean isAvailable() {
        return isAvailable;
    }

    @Override
    public long getTimeRemaining() {
        return -1; // Runes never timeout
    }

    @Override
    public void appear(int x, int y) {
        setPosition(x, y);
        this.isAvailable = true;
    }

    @Override
    public void disappear() {
        this.isAvailable = false;
    }

    // Method for Wizard Monster to teleport the rune
    public void teleport(int newX, int newY) {
        appear(newX, newY);
    }
}