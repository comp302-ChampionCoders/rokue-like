package domain.gameobjects;

import domain.behaviors.Collectible;
public class Rune extends GameObject implements Collectible {
    private boolean isCollected;
    private boolean isAvailable;
    private Point currentPosition;

    public Rune(int x, int y) {
        super(x, y, "Rune");
        this.isAvailable = true;
        this.isCollected = false;
        this.currentPosition = new Point(x,y);
    }

    @Override
    public void collect(Hero hero) {
        if (!isCollected && isAvailable) {
            isCollected = true;
            isAvailable = false;
            setActive(false);
            // hall will handle the unlocking the door
        }
    }
    public boolean isCollected() {return isCollected;}
    @Override
    public boolean canBeCollected() {return !isCollected && isAvailable;}
    @Override
    public String getType() {return "Rune";}
    @Override
    public boolean isAvailable() {return isAvailable;}
    @Override
    public long getTimeRemaining() {return -1;} // Runes never timeout

    @Override
    public void appear(int x, int y) {
        setPosition(x, y);
        this.currentPosition = new Point(x, y);
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
