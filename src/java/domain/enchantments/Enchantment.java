package domain.enchantments;

import domain.gameobjects.*;
import domain.behaviors.Collectible;
import domain.behaviors.GridElement;

public abstract class Enchantment implements Collectible, GridElement {
    private String name; 
    private boolean isActive;
    private boolean isAvailable;
    private int x,y;
    private long spawnTime;
    private static final long DISAPPEAR_TIME = 6000; //6s in ms 

    public Enchantment(String name) {
        this.name = name;
        this.isActive = false;
        this.isAvailable = false;
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
        return isAvailable && !isActive && getTimeRemaining() > 0;
    }

    @Override
    public void appear(int x, int y) {
        this.x = x;
        this.y = y;
        this.isAvailable = true;
        this.spawnTime = System.currentTimeMillis();
    }

    @Override
    public void disappear() {
        this.isAvailable = false;
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
        return isAvailable && getTimeRemaining() > 0;
    }

    @Override
    public long getTimeRemaining() {
        if (!isAvailable) return -1;
        long elapsedTime = System.currentTimeMillis() - spawnTime;
        return Math.max(0, DISAPPEAR_TIME - elapsedTime);
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

