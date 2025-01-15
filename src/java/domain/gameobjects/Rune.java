package domain.gameobjects;

import domain.behaviors.Collectible;
import domain.behaviors.GridElement;

public class Rune implements Collectible, GridElement {
    private int x;
    private int y;
    private boolean isCollected;
    private boolean isAvailable;
    private boolean isHighlighted; // for reveal

    public Rune(int x, int y, Hall hall) {
        this.x = x;
        this.y = y;
        this.isAvailable = true;
        this.isCollected = false;
        this.isHighlighted = false;
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
        System.out.println("Rune position updated to: (" + x + ", " + y + ")"); // debug
    }

    @Override
    public void collect(Hero hero) {
        if (!isCollected && isAvailable) {
            isCollected = true;
            isAvailable = false;
            // hall will handle the unlocking the door
        }
    }

    public void unCollect(Hero hero) {
        if (isCollected && !isAvailable) {
            isCollected = false;
            isAvailable = true;
        }
    }

    public boolean isCollected() {
        return isCollected;
    }

    @Override
    public boolean canBeCollected() {
        return !isCollected && isAvailable;
    }

    public boolean isHighlighted() {
        return isHighlighted;
    }

    public void setHighlighted(boolean highlighted) {
        this.isHighlighted = highlighted;
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
        return;
    }

    // Method for Wizard Monster to teleport the rune
    public void teleport(int newX, int newY) {
        appear(newX, newY);
    }
}