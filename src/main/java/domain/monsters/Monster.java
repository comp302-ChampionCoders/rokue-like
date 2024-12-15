package domain.monsters;

import domain.gameobjects.*;
import domain.enchantments.*;

public abstract class Monster {
    protected int x, y;
    protected String type;
    protected boolean isActive;

    public Monster(int x, int y, String type) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.isActive = true;
    }

    public abstract void performAction(Hero hero); // Abstract method to be implemented by subclasses

    public void moveTowardsHero(Hero hero) {
        if (hero.getX() > this.x) this.x++;
        else if (hero.getX() < this.x) this.x--;
        
        if (hero.getY() > this.y) this.y++;
        else if (hero.getY() < this.y) this.y--;
    }

    public boolean isAdjacentToHero(Hero hero) {
        return Math.abs(hero.getX() - this.x) <= 1 && Math.abs(hero.getY() - this.y) <= 1;
    }

    public String getType() {
        return type;
    }

    public boolean isActive() {
        return isActive;
    }

    public void deactivate() {
        this.isActive = false;
    }

    // Getters and setters for x, y
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }
}
